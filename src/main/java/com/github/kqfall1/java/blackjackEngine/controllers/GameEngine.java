package com.github.kqfall1.java.blackjackEngine.controllers;

import com.github.kqfall1.java.blackjackEngine.model.betting.Bet;
import com.github.kqfall1.java.blackjackEngine.model.betting.Pot;
import com.github.kqfall1.java.blackjackEngine.model.cards.Card;
import com.github.kqfall1.java.blackjackEngine.model.cards.Rank;
import com.github.kqfall1.java.blackjackEngine.model.engine.EngineState;
import com.github.kqfall1.java.blackjackEngine.model.engine.RuleConfig;
import com.github.kqfall1.java.blackjackEngine.model.entities.*;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.IllegalHandOperationException;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.RuleViolationException;
import com.github.kqfall1.java.blackjackEngine.model.hands.PlayerHandType;
import com.github.kqfall1.java.blackjackEngine.model.hands.PlayerHand;
import com.github.kqfall1.java.blackjackEngine.model.interfaces.EngineListener;
import com.github.kqfall1.java.utils.LoggerUtils;
import com.github.kqfall1.java.utils.StringUtils;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.logging.Logger;

/**
 * Orchestrates the entire blackjack library by continuously processing input and
 * executing the main game loop.
 *
 * <p>
 * Centralizes all logic that influences gameplay as a public API, emits all internal
 * events through {@code EngineListener} hooks, and logs pertinent information.
 * </p>
 */
public class GameEngine
{
	private int activeHandPlayerIndex;
	private final RuleConfig config;
	private final Dealer dealer;
	private final EngineListener listener;
	private final Logger logger;
	private final Player player;
	private EngineState state;

	public GameEngine(RuleConfig config, EngineListener listener,
					  String loggerFilePath, String loggerName)
	throws InsufficientChipsException, IOException
	{
		assert config != null : "config == null";
		assert listener != null : "listener == null";
		assert loggerFilePath != null : "loggerFilePath == null";
		assert loggerName != null : "loggerName == null";
		this.config = config;
		dealer = new Dealer();
		this.listener = listener;
		logger = LoggerUtils.newFileLogger(loggerFilePath, loggerName,
			true);
		player = new Player();
		setState(EngineState.START);
	}

	public void acceptInsuranceBet() throws InsufficientChipsException
	{
		getLogger().entering("GameEngine", "acceptInsuranceBet");
		assert getActivePlayerHandIndex() == 0 :  "activeHandPlayerIndex != 0";
		assert getState() == EngineState.INSURANCE_CHECK
			: "getState() != EngineState.INSURANCE_CHECK";
		final var amount = getActivePlayerHand().getPot().getHalf();
		tryDebitForBet(amount);
		final var insurancePot = new Pot(amount);
		final boolean wasSuccessful = getDealer().getHand().isBlackjack();
		final var winnings = insurancePot.scoop();

		if (wasSuccessful)
		{
			getPlayer().setChips(getPlayer().getChips().add(winnings));
			setState(EngineState.SHOWDOWN);
		}

		getListener().onInsuranceBetResolved(wasSuccessful);
		getLogger().exiting("GameEngine", "acceptInsuranceBet");
	}

	public void deal()
	{
		getLogger().entering("GameEngine", "deal");
		assert getState() == EngineState.BETTING
			: "getState() != EngineState.BETTING";
		setState(EngineState.DEALING);
		final var dealerHand = getDealer().getHand();
		assert dealerHand.getCards().isEmpty() : "!dealerHand.getCards().isEmpty()";
		assert getActivePlayerHand().getHand().getCards().isEmpty()
			: "!getActivePlayerHand().getHand().getCards().isEmpty()";
		assert getActivePlayerHandIndex() == 0 :  "activeHandPlayerIndex != 0";

		for (int count = 0; count < RuleConfig.INITIAL_CARD_COUNT; count++)
		{
			dealCardForPlayer();
			dealCardForDealer();
		}
		assert getActivePlayerHand().getHand().getCards().size() == RuleConfig.INITIAL_CARD_COUNT
			: "getActivePlayerHand().getHand().getCards().size() != RuleConfig.INITIAL_CARD_COUNT";
		assert dealerHand.getCards().size() == RuleConfig.INITIAL_CARD_COUNT
			: "dealerHand.getCards().size() != RuleConfig.INITIAL_CARD_COUNT";

		getLogger().info(String.format(
			"The cards have been dealt. Player's hand: %s. The dealer's up card is %s.",
			getActivePlayerHand(),
			dealerHand.getCards().get(RuleConfig.INITIAL_CARD_COUNT - 1)
		));
		getLogger().exiting("GameEngine", "deal");

		if (getInsuranceBetPossible())
		{
			setState(EngineState.INSURANCE_CHECK);
			getListener().onInsuranceBetOpportunityDetected();
		}
	}

	private void dealCardForDealer()
	{
		getLogger().entering("GameEngine", "dealCardForDealer");
		assert getState() == EngineState.DEALING || getState() == EngineState.DEALER_TURN
			: "getState() != EngineState.DEALING && getState() != EngineState.DEALER_TURN";
		final var card = getDealer().hit();
		getDealer().getHand().addCard(card);
		onCardDealtToDealer(card);
		getLogger().exiting("GameEngine", "dealCardForDealer", card);
	}

	private void dealCardForPlayer()
	{
		getLogger().entering("GameEngine", "dealCardForPlayer");
		assert getState() == EngineState.DEALING : "getState() != EngineState.DEALING";
		final var card = getDealer().hit();
		getActivePlayerHand().getHand().addCard(card);
		onCardDealtToPlayer(card);
		getLogger().exiting("GameEngine", "dealCardForPlayer", card);
	}

	private void dealerTurn()
	{
		getLogger().entering("GameEngine", "dealerTurn");
		assert getActivePlayerHandIndex() == 0 :  "activeHandPlayerIndex != 0";
		assert getState() == EngineState.DEALER_TURN
			: "getState() != EngineState.DEALER_TURN";
		while (getShouldDealerPlay())
		{
			dealCardForDealer();
		}
		onDrawingRoundCompletedDealer();
		getLogger().exiting("GameEngine", "dealerTurn");
	}

	public void declineInsuranceBet()
	{
		getLogger().entering("GameEngine", "declineInsuranceBet");
		assert getActivePlayerHandIndex() == 0 :  "activeHandPlayerIndex != 0";
		assert getState() == EngineState.INSURANCE_CHECK
			: "getState() != EngineState.INSURANCE_CHECK";
		setState(EngineState.PLAYER_TURN);
		getLogger().exiting("GameEngine", "declineInsuranceBet");
	}

	public PlayerHand getActivePlayerHand()
	{
		return getPlayer().getHands().get(getActivePlayerHandIndex());
	}

	public int getActivePlayerHandIndex()
	{
		return activeHandPlayerIndex;
	}

	public RuleConfig getConfig()
	{
		return config;
	}

	public Dealer getDealer()
	{
		return dealer;
	}

	private boolean getInsuranceBetPossible()
	{
		return getActivePlayerHandIndex() == 0
			&& getPlayer().getHands().size() == 1
			&& getActivePlayerHand().getHand().getCards().size() == RuleConfig.INITIAL_CARD_COUNT
			&& getPlayer().getChips().compareTo(getActivePlayerHand().getPot().getHalf()) >= 0
			&& getDealer().getHand().getCards().getLast().getRank() == Rank.ACE;
	}

	public EngineListener getListener()
	{
		return listener;
	}

	public Logger getLogger()
	{
		return logger;
	}

	public Player getPlayer()
	{
		return player;
	}

	private boolean getShouldDealerPlay()
	{
		final int MINIMUM_SCORE_TO_STAND = getConfig().getDealerHitsOnSoft17()
			? RuleConfig.TOP_SCORE + 1
			: RuleConfig.TOP_SCORE;
		return getDealer().getHand().getScore() < MINIMUM_SCORE_TO_STAND;
	}

	public EngineState getState()
	{
		return state;
	}

	private void onCardDealtToDealer(Card card)
	{
		getLogger().entering("GameEngine", "onCardDealtToDealer", card);
		assert card != null : "card == null";
		assert getState() == EngineState.DEALING || getState() == EngineState.DEALER_TURN
			: "getState() != EngineState.DEALING && getState() != EngineState.DEALER_TURN";
		getListener().onCardDealtToDealer(card, getDealer().getHand());
		getLogger().info(String.format(
			"Added card %s to dealer's hand %s.",
			card, getDealer().getHand()
		));
		getLogger().exiting("GameEngine", "onCardDealtToDealer");
	}

	private void onCardDealtToPlayer(Card card)
	{
		getLogger().entering("GameEngine", "onCardDealtToPlayer", card);
		assert card != null : "card == null";
		assert getState() == EngineState.DEALING : "getState() != EngineState.DEALING";
		getListener().onCardDealtToPlayer(card, getActivePlayerHand());
		getLogger().info(String.format(
			"Added card %s to player's hand %s.",
			card, getActivePlayerHand()
		));
		if (getActivePlayerHand().getHand().isBusted())
		{
			onDrawingRoundCompletedPlayer();
		}
		getLogger().exiting("GameEngine", "onCardDealtToPlayer");
	}

	private void onDrawingRoundCompletedDealer()
	{
		getLogger().entering("GameEngine", "onDrawingRoundCompletedDealer");
		assert getActivePlayerHandIndex() == 0 :  "activeHandPlayerIndex != 0";
		assert getState() == EngineState.DEALER_TURN
			: "getState() != EngineState.DEALER_TURN";
		getListener().onDrawingRoundCompletedDealer();
		getLogger().info("The dealer's drawing round was completed.");
		setState(EngineState.SHOWDOWN);
		showdown();
		getLogger().exiting("GameEngine", "onDrawingRoundCompletedDealer");
	}

	private void onDrawingRoundCompletedPlayer()
	{
		getLogger().entering("GameEngine", "onDrawingRoundCompletedPlayer");
		assert getState() == EngineState.PLAYER_TURN
			: "getState() != EngineState.PLAYER_TURN";
		getListener().onDrawingRoundCompletedPlayer(getActivePlayerHand());
		if (getActivePlayerHand().getType() == PlayerHandType.MAIN)
		{
			assert getActivePlayerHandIndex() == 0 : "activeHandPlayerIndex != 0";
			if (getActivePlayerHand().getHand().isBusted())
			{
				setState(EngineState.SHOWDOWN);
				getLogger().info(String.format(
					"Player has busted with a score of %d on hand %s.",
					getActivePlayerHand().getHand().getScore(),
					getActivePlayerHand().getHand()
				));
				showdown();
			}
			else if (getActivePlayerHand().getHasSurrendered())
			{
				setState(EngineState.SHOWDOWN);
				getLogger().info(String.format(
					"Player has surrendered on hand %s.",
					getActivePlayerHand().getHand()
				));
				showdown();
			}
			else
			{
				setState(EngineState.DEALER_TURN);
				dealerTurn();
			}
		}
		else
		{
			assert getActivePlayerHandIndex() != 0 : "activeHandPlayerIndex == 0";
			setActivePlayerHandIndex(getActivePlayerHandIndex() - 1);
		}
		getLogger().info(String.format(
			"The player's drawing round was completed for hand %s",
			getActivePlayerHand()
		));
		getLogger().exiting("GameEngine", "onDrawingRoundCompletedPlayer");
	}

	private void onMainBetPlaced()
	{
		getLogger().entering("GameEngine", "onBetPlaced");
		assert getActivePlayerHandIndex() == 0 :  "activeHandPlayerIndex != 0";
		assert getState() == EngineState.BETTING : "getState() != EngineState.BETTING";
		getListener().onBetPlaced(getPlayer(), getActivePlayerHand());
		getLogger().info(String.format(
			"Player %s has placed a bet of $%.2f on their %s hand.",
			getPlayer(),
			getActivePlayerHand().getBet().getAmount(),
			StringUtils.normalizeLower(getActivePlayerHand().getType().toString())
		));
		getLogger().exiting("GameEngine", "onBetPlaced");
	}

	public void placeMainBet(BigDecimal amount) throws InsufficientChipsException
	{
		getLogger().entering("GameEngine", "placeBet", amount);
		assert amount != null && amount.compareTo(BigDecimal.ZERO) > 0
			: "amount == null || amount.compareTo(BigDecimal.ZERO) <= 0";
		assert getActivePlayerHandIndex() == 0 :  "activeHandPlayerIndex != 0";
		assert getState() == EngineState.BETTING
				|| getState() == EngineState.RESETTING
				|| getState() == EngineState.START
			: "getState() != EngineState.BETTING && getState() != EngineState.RESETTING && getState() != EngineState.START";
		setState(EngineState.BETTING);
		tryDebitForBet(amount);

		final var playerMainHand = new PlayerHand(
			new Bet(amount),
			PlayerHandType.MAIN
		);
		getPlayer().addHand(playerMainHand);
		getActivePlayerHand().getPot().addChips(amount);
		onMainBetPlaced();
		getLogger().exiting("GameEngine", "placeBet", playerMainHand);
		deal();
	}

	public void playerDoubleDown(int handIndex)
	throws IllegalHandOperationException, InsufficientChipsException,
	RuleViolationException
	{
		getLogger().entering("GameEngine",  "playerDoubleDown");
		assert getState() == EngineState.PLAYER_TURN
			: "getState() != EngineState.PLAYER_TURN";
		if (handIndex != getActivePlayerHandIndex())
		{
			return;
		}

		final var playerHand = getActivePlayerHand();
		if (playerHand.getHand().getCards().size()
			!= RuleConfig.INITIAL_CARD_COUNT)
		{
			throw new IllegalHandOperationException(
				playerHand.getHand(),
				String.format(
					"An attempt to double down occurred on a hand with more than %d cards",
					RuleConfig.INITIAL_CARD_COUNT
				)
			);
		}
		else if (playerHand.getType() != PlayerHandType.MAIN
			&& !getConfig().getPlayerCanDoubleDownOnSplitHands())
		{
			throw new RuleViolationException("Player cannot double down on split hands.");
		}
		final var doubleDownAmount = getActivePlayerHand().getBet().getAmount();
		tryDebitForBet(doubleDownAmount);

		playerHand.setBet(
			new Bet(
				doubleDownAmount.multiply(BigDecimal.TWO)
			)
		);
		playerHit(handIndex);
		getLogger().info(String.format(
			"Player has doubled down on hand %s",
			playerHand.getHand()
		));
		getLogger().exiting("GameEngine", "playerDoubleDown");
	}

	public void playerHit(int handIndex)
	{
		getLogger().entering("GameEngine", "playerHit");
		assert getState() == EngineState.PLAYER_TURN
			: "getState() != EngineState.PLAYER_TURN";
		if (handIndex != getActivePlayerHandIndex())
		{
			return;
		}
		dealCardForPlayer();
		getLogger().exiting("GameEngine", "playerHit");
	}
	
	public void playerSplit()
	throws IllegalHandOperationException, InsufficientChipsException
	{
		getLogger().entering("GameEngine", "playerSplit");
		assert getActivePlayerHandIndex() == 0 :  "activeHandPlayerIndex != 0";
		assert getState() == EngineState.PLAYER_TURN
			: "getState() != EngineState.PLAYER_TURN";
		final var playerMainHand = getPlayer().getHands().getFirst();
		if (playerMainHand.getHand().getCards().size() != RuleConfig.INITIAL_CARD_COUNT
			|| !playerMainHand.getHand().isPocketPair())
		{
			throw new IllegalHandOperationException(
				playerMainHand.getHand(),
				"An attempt to split a non-pocket pair occurred."
			);
		}
		final var splitAmount = getActivePlayerHand().getPot().getAmount();
		tryDebitForBet(splitAmount);

		final var playerSplitHand = new PlayerHand(
			new Bet(splitAmount),
			PlayerHandType.SPLIT
		);
		playerSplitHand.getHand().addCard(playerMainHand.getHand().getCards().getLast());
		getPlayer().addHand(playerSplitHand);
		playerMainHand.getHand().removeCard(RuleConfig.INITIAL_CARD_COUNT - 1);
		setActivePlayerHandIndex(getActivePlayerHandIndex() + 1);
		getLogger().info(String.format(
			"Player has elected to split. They now have a main hand of %s and a split hand of %s.",
			getPlayer().getHands().getFirst(),
			playerSplitHand
		));
		getLogger().exiting("GameEngine", "playerSplit");
	}

	public void playerStand(int handIndex)
	{
		getLogger().entering("GameEngine", "playerStand");
		assert getState() == EngineState.PLAYER_TURN
			: "getState() != EngineState.PLAYER_TURN";
		if (handIndex != getActivePlayerHandIndex())
		{
			return;
		}
		onDrawingRoundCompletedPlayer();
		getLogger().exiting("GameEngine", "playerStand");
	}

	public void playerSurrender(int handIndex) throws RuleViolationException
	{
		getLogger().entering("GameEngine", "playerSurrender");
		assert getState() == EngineState.PLAYER_TURN
			: "getState() != EngineState.PLAYER_TURN";
		if (handIndex != getActivePlayerHandIndex())
		{
			return;
		}
		else if (getActivePlayerHand().getType() == PlayerHandType.SPLIT)
		{
			if (!getConfig().getPlayerCanSurrenderOnSplitHands())
			{
				throw new RuleViolationException("Player cannot surrender on split hands.");
			}
		}

		getActivePlayerHand().setHasSurrendered(true);
		onDrawingRoundCompletedPlayer();
		getLogger().exiting("GameEngine", "playerSurrender");
	}

	private void reset()
	{

	}

	private void setActivePlayerHandIndex(int handIndex)
	{
		getLogger().entering("GameEngine", "setActiveHandIndex", handIndex);
		assert getState() == EngineState.PLAYER_TURN
			: "getState() != EngineState.PLAYER_TURN";
		assert handIndex >= 0 && handIndex < getPlayer().getHands().size()
			: "handIndex < 0 && handIndex >= getPlayer().getHands().size()";
		activeHandPlayerIndex = handIndex;
	}

	private void setState(EngineState state)
	{
		getLogger().entering("GameEngine", "setState", state);
		assert state != null : "state == null";
		final var oldState = getState();
		this.state = state;
		getListener().onStateChanged(oldState, state);
		getLogger().exiting("GameEngine", "setState");
	}

	private void showdown()
	{

	}

	@Override
	public String toString()
	{
		final var NULL_STRING = "null";
		return String.format(
			"%s[config=%s,dealer=%s,listener=%s,logger=%s,player=%s,state=%s]",
			getClass().getName(),
			getConfig(),
			getDealer(),
			getListener(),
			getLogger(),
			getPlayer(),
			getState()
		);
	}

	/**
 	 * Attempts to subtract a given amount from the {@code Player} object's {@code chips}.
 	 * @param amount The amount to subtract.
	 * @throws InsufficientChipsException if the {@code Player} has insufficient {@code chips}.
 	 */
	private void tryDebitForBet(BigDecimal amount) throws InsufficientChipsException
	{
		try
		{
			getPlayer().setChips(getPlayer().getChips().subtract(amount));
		}
		catch (InsufficientChipsException e)
		{
			getLogger().throwing("GameEngine", "placeMainBet", e);
			throw e;
		}
	}
}