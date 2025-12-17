package com.github.kqfall1.java.blackjackEngine.controllers;

import com.github.kqfall1.java.blackjackEngine.model.betting.Bet;
import com.github.kqfall1.java.blackjackEngine.model.betting.Pot;
import com.github.kqfall1.java.blackjackEngine.model.cards.Card;
import com.github.kqfall1.java.blackjackEngine.model.cards.Deck;
import com.github.kqfall1.java.blackjackEngine.model.cards.Rank;
import com.github.kqfall1.java.blackjackEngine.model.engine.EngineState;
import com.github.kqfall1.java.blackjackEngine.model.engine.StandardRuleConfig;
import com.github.kqfall1.java.blackjackEngine.model.entities.*;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.IllegalHandOperationException;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.RuleViolationException;
import com.github.kqfall1.java.blackjackEngine.model.hands.Hand;
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
	/**
 	 * Identifies the {@code PlayerHand} object in the encapsulated {@code Player} object's
	 * {@code hands} property that is currently being acted upon by this {@code GameEngine} in a
	 * blackjack betting round.
	 *
	 * <p>
	 * During the {@code EngineState.PLAYER_TURN} state, it indicates the {@code PlayerHand} that
	 * the player is actively making decisions on; during other states, it remains at 0.
	 * </p>
 	 */
	private int activeHandPlayerIndex;
	private final StandardRuleConfig config;
	private final Dealer dealer;
	private final EngineListener listener;
	private final Logger logger;
	private final Player player;
	private static final String RULE_VIOLATION_MESSAGE = "A blackjack rule was violated.";
	private EngineState state;

	public GameEngine(StandardRuleConfig config, EngineListener listener,
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

	public void acceptInsuranceBet()
	throws IllegalHandOperationException, InsufficientChipsException, RuleViolationException
	{
		getLogger().entering("GameEngine", "acceptInsuranceBet");
		assert getActivePlayerHandIndex() == 0 :  "activeHandPlayerIndex != 0";
		assert getState() == EngineState.INSURANCE_CHECK
			: "getState() != EngineState.INSURANCE_CHECK";
		if (!getConfig().isInsuranceBetPossible(getActivePlayerHand(), getPlayer(),
			getDealer().getHand()))
		{
			if (getActivePlayerHand().isAltered())
			{
				throw new IllegalHandOperationException(
					getActivePlayerHand().getHand(),
					String.format(
						"Player cannot place an insurance side bet with a hand with more than %d cards.",
						StandardRuleConfig.INITIAL_CARD_COUNT
					)
				);
			}
			else if (getDealer().getHand().getCards().getLast().getRank() != Rank.ACE)
			{
				throw new IllegalHandOperationException(
					getActivePlayerHand().getHand(),
					"Player cannot place an insurance side bet when the dealer's up card isn't an ace."
				);
			}
			else if (getPlayer().getHands().size() != 1)
			{
				throw new IllegalHandOperationException(
					getActivePlayerHand().getHand(),
					"Player cannot place an insurance side bet when they have already split."
				);
			}
			else if (getPlayer().getChips().compareTo(getActivePlayerHand().getBet().getHalf()) < 0)
			{
				throw new InsufficientChipsException(
					getPlayer(),
					getActivePlayerHand().getBet().getHalf()
				);
			}
			else
			{
				throw new RuleViolationException(RULE_VIOLATION_MESSAGE);
			}
		}
		final var amount = getActivePlayerHand().getBet().getHalf();
		getPlayer().setChips(getPlayer().getChips().subtract(amount));
		final var insurancePot = new Pot(amount);
		final boolean wasSuccessful = getDealer().getHand().isBlackjack();
		final var winnings = insurancePot.scoop().multiply(
			StandardRuleConfig.INSURANCE.getPayoutMultiplier()
		);

		if (wasSuccessful)
		{
			getPlayer().setChips(getPlayer().getChips().add(winnings));
			setState(EngineState.SHOWDOWN);
		}
		else
		{
			setState(EngineState.PLAYER_TURN);
			onDrawingRoundStartedPlayer();
		}

		getListener().onInsuranceBetResolved(wasSuccessful);
		getLogger().exiting("GameEngine", "acceptInsuranceBet");
	}

	public void deal() throws InsufficientChipsException
	{
		getLogger().entering("GameEngine", "deal");
		assert getState() == EngineState.DEALING
			: "getState() != EngineState.DEALING";
		final var dealerHand = getDealer().getHand();
		assert dealerHand.getCards().isEmpty() : "!dealerHand.getCards().isEmpty()";
		assert getActivePlayerHand().getHand().getCards().isEmpty()
			: "!getActivePlayerHand().getHand().getCards().isEmpty()";
		assert getActivePlayerHandIndex() == 0 :  "activeHandPlayerIndex != 0";

		for (int count = 0; count < StandardRuleConfig.INITIAL_CARD_COUNT; count++)
		{
			dealCardForPlayer();
			dealCardForDealer();
		}
		assert getActivePlayerHand().getHand().getCards().size() == StandardRuleConfig.INITIAL_CARD_COUNT
			: "getActivePlayerHand().getHand().getCards().size() != RuleConfig.INITIAL_CARD_COUNT";
		assert dealerHand.getCards().size() == StandardRuleConfig.INITIAL_CARD_COUNT
			: "dealerHand.getCards().size() != RuleConfig.INITIAL_CARD_COUNT";

		getLogger().info(String.format(
			"The cards have been dealt. Player's hand: %s. The dealer's up card is %s.",
			getActivePlayerHand(),
			dealerHand.getCards().get(StandardRuleConfig.INITIAL_CARD_COUNT - 1)
		));
		getLogger().exiting("GameEngine", "deal");

		if (getConfig().isInsuranceBetPossible(
			getActivePlayerHand(), getPlayer(), getDealer().getHand()))
		{
			setState(EngineState.INSURANCE_CHECK);
			getListener().onInsuranceBetOpportunityDetected();
		}
		else
		{
			setState(EngineState.PLAYER_TURN);
			onDrawingRoundStartedPlayer();
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

	private void dealCardForPlayer() throws InsufficientChipsException
	{
		getLogger().entering("GameEngine", "dealCardForPlayer");
		assert getState() == EngineState.DEALING : "getState() != EngineState.DEALING";
		final var card = getDealer().hit();
		getActivePlayerHand().getHand().addCard(card);
		onCardDealtToPlayer(card);
		getLogger().exiting("GameEngine", "dealCardForPlayer", card);
	}

	private void dealerTurn() throws InsufficientChipsException
	{
		getLogger().entering("GameEngine", "dealerTurn");
		assert getActivePlayerHandIndex() == 0 :  "activeHandPlayerIndex != 0";
		assert getState() == EngineState.DEALER_TURN
			: "getState() != EngineState.DEALER_TURN";
		onDrawingRoundStartedDealer();
		while (getConfig().isDealerTurnActive(getState(), getDealer()))
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

	/**
	 * Retrieves the {@code PlayerHand} object corresponding to {@code activePlayerHandIndex}.
	 *
	 * <p>
	 * This method is safe to call in all {@code EngineState} states as long as the
	 * {@code Player} possesses at least one non-null {@code PlayerHand}.
	 * </p>
 	 */
	public PlayerHand getActivePlayerHand()
	{
		return getPlayer().getHands().get(getActivePlayerHandIndex());
	}

	public int getActivePlayerHandIndex()
	{
		return activeHandPlayerIndex;
	}

	public StandardRuleConfig getConfig()
	{
		return config;
	}

	public Dealer getDealer()
	{
		return dealer;
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
		getListener().onCardDealtToDealer(card);
		getLogger().info(String.format(
			"Added card %s to dealer's hand %s.",
			card, getDealer().getHand()
		));
		getLogger().exiting("GameEngine", "onCardDealtToDealer");
	}

	private void onCardDealtToPlayer(Card card) throws InsufficientChipsException
	{
		getLogger().entering("GameEngine", "onCardDealtToPlayer", card);
		assert card != null : "card == null";
		assert getState() == EngineState.DEALING || getState() == EngineState.PLAYER_TURN
			: "getState() != EngineState.DEALING && getState() != EngineState.PLAYER_TURN";
		getListener().onCardDealtToPlayer(card);
		if (getState() != EngineState.DEALING)
		{
			getActivePlayerHand().markAsAltered();
		}
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

	private void onDrawingRoundCompletedDealer() throws InsufficientChipsException
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

	private void onDrawingRoundCompletedPlayer() throws InsufficientChipsException
	{
		getLogger().entering("GameEngine", "onDrawingRoundCompletedPlayer");
		assert getState() == EngineState.PLAYER_TURN
			: "getState() != EngineState.PLAYER_TURN";
		getListener().onDrawingRoundCompletedPlayer();
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

	private void onDrawingRoundStartedDealer()
	{
		getLogger().entering("GameEngine", "onDrawingRoundStartedDealer");
		assert getActivePlayerHandIndex() == 0 :  "activeHandPlayerIndex != 0";
		assert getState() == EngineState.DEALER_TURN : "getState() != EngineState.DEALER_TURN";
		getListener().onDrawingRoundStartedDealer();
		getLogger().info("The dealer's drawing round was started.");
		getLogger().exiting("GameEngine", "onDrawingRoundStartedDealer");
	}

	private void onDrawingRoundStartedPlayer()
	{
		getLogger().entering("GameEngine", "onDrawingRoundStartedPlayer");
		assert getState() == EngineState.PLAYER_TURN : "getState() != EngineState.PLAYER_TURN";
		getListener().onDrawingRoundStartedPlayer();
		getLogger().info(String.format(
			"The player's drawing round was started on hand %s.",
			getActivePlayerHand()
		));
		getLogger().exiting("GameEngine", "onDrawingRoundStartedPlayer");
	}

	private void onBetPlaced()
	{
		getLogger().entering("GameEngine", "onBetPlaced");
		assert getActivePlayerHandIndex() == 0 :  "activeHandPlayerIndex != 0";
		assert getState() == EngineState.BETTING : "getState() != EngineState.BETTING";
		getListener().onBetPlaced();
		getLogger().info(String.format(
			"Player %s has placed a bet of $%.2f on their %s hand.",
			getPlayer(),
			getActivePlayerHand().getBet().getAmount(),
			StringUtils.normalizeLower(getActivePlayerHand().getType().toString())
		));
		getLogger().exiting("GameEngine", "onBetPlaced");
	}

	public void placeBet(BigDecimal amount) throws InsufficientChipsException
	{
		getLogger().entering("GameEngine", "placeBet", amount);
		assert amount != null && amount.compareTo(BigDecimal.ZERO) > 0
			: "amount == null || amount.compareTo(BigDecimal.ZERO) <= 0";
		assert getActivePlayerHandIndex() == 0 :  "activeHandPlayerIndex != 0";
		assert getState() == EngineState.BETTING || getState() == EngineState.START
			: "getState() != EngineState.BETTING && getState() != EngineState.START";
		if (getPlayer().getChips().compareTo(amount) < 0)
		{
			throw new InsufficientChipsException(
				getPlayer(),
				amount
			);
		}
		else if (getState() == EngineState.START)
		{
			getListener().onGameStarted();
		}
		setState(EngineState.BETTING);
		getListener().onBettingRoundStarted();
		final var playerMainHand = new PlayerHand(
			new Bet(amount),
			PlayerHandType.MAIN
		);
		getPlayer().addHand(playerMainHand);
		getPlayer().setChips(getPlayer().getChips().subtract(amount));
		getActivePlayerHand().getPot().addChips(amount.multiply(BigDecimal.TWO));
		onBetPlaced();
		setState(EngineState.DEALING);
		getLogger().exiting("GameEngine", "placeBet", playerMainHand);
		deal();
	}

	public void playerDoubleDown(int handIndex)
	throws IllegalHandOperationException, InsufficientChipsException, RuleViolationException
	{
		getLogger().entering("GameEngine",  "playerDoubleDown");
		assert handIndex == getActivePlayerHandIndex() : "handIndex = getActivePlayerHandIndex()";
		assert getState() == EngineState.PLAYER_TURN
			: "getState() != EngineState.PLAYER_TURN";
		final var playerHand = getActivePlayerHand();
		if (!getConfig().isDoubleDownPossible(getActivePlayerHand(), getPlayer()))
		{
			if (getActivePlayerHand().isAltered())
			{
				throw new IllegalHandOperationException(
					playerHand.getHand(),
					String.format(
						"Player cannot double down on a hand with more than %d cards",
						StandardRuleConfig.INITIAL_CARD_COUNT
					)
				);
			}
			else if (getPlayer().getChips().compareTo(getActivePlayerHand().getBet().getAmount()) < 0)
			{
				throw new InsufficientChipsException(
					getPlayer(),
					getActivePlayerHand().getBet().getAmount()
				);
			}
			else
			{
				throw new RuleViolationException(RULE_VIOLATION_MESSAGE);
			}
		}
		final var doubleDownAmount = getActivePlayerHand().getBet().getAmount();
		getPlayer().setChips(getPlayer().getChips().subtract(doubleDownAmount));
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
		if (!playerHand.getHand().isBusted())
		{
			onDrawingRoundCompletedPlayer();
		}
		getLogger().exiting("GameEngine", "playerDoubleDown");
	}

	public void playerHit(int handIndex) throws InsufficientChipsException
	{
		getLogger().entering("GameEngine", "playerHit");
		assert handIndex == getActivePlayerHandIndex() : "handIndex = getActivePlayerHandIndex()";
		assert getState() == EngineState.PLAYER_TURN
			: "getState() != EngineState.PLAYER_TURN";
		dealCardForPlayer();
		getLogger().exiting("GameEngine", "playerHit");
	}
	
	public void playerSplit()
	throws IllegalHandOperationException, InsufficientChipsException, RuleViolationException
	{
		getLogger().entering("GameEngine", "playerSplit");
		assert getActivePlayerHandIndex() == 0 :  "activeHandPlayerIndex != 0";
		assert getState() == EngineState.PLAYER_TURN
			: "getState() != EngineState.PLAYER_TURN";
		final var playerMainHand = getActivePlayerHand();
		if (!getConfig().isSplitPossible(getActivePlayerHand(), getActivePlayerHandIndex(),
			getPlayer()))
		{
			if (getActivePlayerHand().isAltered()
				|| !playerMainHand.getHand().isPocketPair())
			{
				throw new IllegalHandOperationException(
					playerMainHand.getHand(),
					"An attempt to split a non-pocket pair occurred."
				);
			}
			else if (getActivePlayerHandIndex() + 1 >= StandardRuleConfig.MAXIMUM_PLAYER_HANDS_PER_BETTING_ROUND)
			{
				throw new IllegalHandOperationException(
					getActivePlayerHand().getHand(),
					String.format(
						"Player cannot have more than %d hands.",
						StandardRuleConfig.MAXIMUM_PLAYER_HANDS_PER_BETTING_ROUND
					)
				);
			}
			else if (getPlayer().getChips().compareTo(getActivePlayerHand().getBet().getAmount()) < 0)
			{
				throw new InsufficientChipsException(
					getPlayer(),
					getActivePlayerHand().getBet().getAmount()
				);
			}
			else
			{
				throw new RuleViolationException(RULE_VIOLATION_MESSAGE);
			}
		}
		final var splitAmount = getActivePlayerHand().getBet().getAmount();
		getPlayer().setChips(getPlayer().getChips().subtract(splitAmount));
		final var playerSplitHand = new PlayerHand(
			new Bet(splitAmount),
			PlayerHandType.SPLIT
		);
		playerSplitHand.getHand().addCard(playerMainHand.getHand().getCards().getLast());
		getPlayer().addHand(playerSplitHand);
		playerMainHand.getHand().removeCard(StandardRuleConfig.INITIAL_CARD_COUNT - 1);
		playerMainHand.markAsAltered();
		playerMainHand.getHand().addCard(getDealer().getDeck().draw());
		playerSplitHand.getHand().addCard(getDealer().getDeck().draw());
		setActivePlayerHandIndex(getActivePlayerHandIndex() + 1);
		onDrawingRoundStartedPlayer();
		getLogger().info(String.format(
			"Player has elected to split. Player now has a main hand of %s and a split hand of %s.",
			getPlayer().getHands().getFirst(),
			playerSplitHand
		));
		getLogger().exiting("GameEngine", "playerSplit");
	}

	public void playerStand(int handIndex) throws InsufficientChipsException
	{
		getLogger().entering("GameEngine", "playerStand");
		assert handIndex == getActivePlayerHandIndex() : "handIndex = getActivePlayerHandIndex()";
		assert getState() == EngineState.PLAYER_TURN
			: "getState() != EngineState.PLAYER_TURN";
		getActivePlayerHand().markAsAltered();
		onDrawingRoundCompletedPlayer();
		getLogger().exiting("GameEngine", "playerStand");
	}

	public void playerSurrender(int handIndex)
	throws IllegalHandOperationException, InsufficientChipsException, RuleViolationException
	{
		getLogger().entering("GameEngine", "playerSurrender");
		assert handIndex == getActivePlayerHandIndex() : "handIndex = getActivePlayerHandIndex()";
		assert getState() == EngineState.PLAYER_TURN
			: "getState() != EngineState.PLAYER_TURN";
		if (!getConfig().isSurrenderPossible(getActivePlayerHand(), getPlayer()))
		{
			if (getActivePlayerHand().isAltered())
			{
				throw new IllegalHandOperationException(
					getActivePlayerHand().getHand(),
					String.format(
						"Player cannot surrender on a hand with more than %d cards",
						StandardRuleConfig.INITIAL_CARD_COUNT
					)
				);
			}
			else
			{
				throw new RuleViolationException(RULE_VIOLATION_MESSAGE);
			}
		}
		getActivePlayerHand().setHasSurrendered(true);
		onDrawingRoundCompletedPlayer();
		getLogger().exiting("GameEngine", "playerSurrender");
	}

	private void reset()
	{
		getLogger().entering("GameEngine", "reset");
		assert getActivePlayerHandIndex() == 0 : "activeHandPlayerIndex != 0";
		assert getState() == EngineState.RESETTING : "getState() != EngineState.RESETTING";
		getDealer().setDeck(new Deck());
		getDealer().setHand(new Hand());
		getPlayer().clearHands();
		getListener().onReset();
		if (getConfig().isGameActive(getPlayer()))
		{
			setState(EngineState.BETTING);
		}
		else
		{
			setState(EngineState.END);
			getListener().onGameCompleted();
			getLogger().info("The player has busted.");
		}
		getLogger().exiting("GameEngine", "reset");
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
		getListener().onStateChanged(oldState);
		getLogger().exiting("GameEngine", "setState");
	}

	private void showdown() throws InsufficientChipsException
	{
		getLogger().entering("GameEngine", "showdown");
		assert getActivePlayerHandIndex() == 0 :  "activeHandPlayerIndex != 0";
		assert getState() == EngineState.SHOWDOWN : "getState() != EngineState.SHOWDOWN";
		for (PlayerHand playerHand : getPlayer().getHands())
		{
			getListener().onShowdownStarted();
			var playerBeatDealer = false;
			var playerWinnings = BigDecimal.ZERO;
			if (playerHand.getHasSurrendered())
			{
				playerWinnings = playerHand.getPot().scoop().multiply(
					StandardRuleConfig.SURRENDER.getPayoutMultiplier()
				);
			}
			else if (playerHand.getHand().isBusted())
			{
				playerHand.getPot().scoop();
			}
			else if (getDealer().getHand().isBusted())
			{
				playerBeatDealer = true;
				playerWinnings = playerHand.getPot().scoop();
			}
			else
			{
				if (playerHand.getHand().getScore() == getDealer().getHand().getScore())
				{
					playerWinnings = playerHand.getPot().scoop().multiply(
						StandardRuleConfig.PUSH.getPayoutMultiplier()
					);
				}
				else if (playerHand.getHand().getScore() > getDealer().getHand().getScore())
				{
					playerBeatDealer = true;

					if (playerHand.getHand().isBlackjack())
					{
						playerWinnings = playerHand.getPot().scoop().multiply(
							StandardRuleConfig.BLACKJACK.getPayoutMultiplier()
						);
					}
					else
					{
						playerWinnings = playerHand.getPot().scoop();
					}
				}
			}
			getPlayer().setChips(getPlayer().getChips().add(playerWinnings));
			getLogger().info(String.format(
				"Player's hand %s was showed down against dealer's hand %s.",
				playerHand, getDealer().getHand()
			));
			getListener().onShowdownCompleted(playerBeatDealer);
		}
		getListener().onBettingRoundCompleted();
		setState(EngineState.RESETTING);
		reset();
		getLogger().exiting("GameEngine", "showdown");
	}

	@Override
	public String toString()
	{
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
}