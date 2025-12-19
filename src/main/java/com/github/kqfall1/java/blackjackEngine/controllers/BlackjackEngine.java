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
 * Centralizes all core logic that influences gameplay as a public API, emits all internal
 * events through {@code EngineListener} hooks, and logs pertinent information.
 * </p>
 */
public class BlackjackEngine
{
	/**
 	 * Identifies the {@code PlayerHand} object in the encapsulated {@code Player} object's
	 * {@code hands} property that is currently being acted upon by this {@code BlackjackEngine}
	 * in a blackjack betting round.
	 *
	 * <p>
	 * During the {@code EngineState.PLAYER_TURN} state, it indicates the {@code PlayerHand} that
	 * the player is actively making decisions on; during other states, it remains at 0.
	 * </p>
 	 */
	private int activeHandPlayerIndex;
	public static final String CLASS_NAME = "BlackjackEngine";
	private final StandardRuleConfig config;
	private final Dealer dealer;
	private final EngineListener listener;
	private final Logger logger;
	private final Player player;
	private static final String RULE_VIOLATION_MESSAGE = "A blackjack rule was violated.";
	private EngineState state;

	public BlackjackEngine(StandardRuleConfig config, EngineListener listener,
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
		getPlayer().setChips(getConfig().getPlayerInitialChips());
		state = EngineState.START;
	}

	public void acceptInsuranceBet() throws Exception
	{
		final var METHOD_NAME = "acceptInsuranceBet";
		getLogger().entering(CLASS_NAME, METHOD_NAME);
		assert getActivePlayerHandIndex() == 0 :  "activeHandPlayerIndex != 0";
		assert getState() == EngineState.INSURANCE_CHECK
			: "getState() != EngineState.INSURANCE_CHECK";
		if (!getConfig().isInsuranceBetPossible(getActivePlayerHand(), getPlayer(),
			getDealer().getHand()))
		{
			if (getActivePlayerHand().isAltered())
			{
			  	throwException(new IllegalHandOperationException(
					getActivePlayerHand(),
					String.format(
						"Player cannot place an insurance side bet with a hand with more than %d cards.",
						StandardRuleConfig.INITIAL_CARD_COUNT
					)
				), METHOD_NAME);
			}
			else if (getDealer().getHand().getCards().getLast().getRank() != Rank.ACE)
			{
				throwException(new IllegalHandOperationException(
					getActivePlayerHand(),
					"Player cannot place an insurance side bet when the dealer's up card isn't an ace."
				), METHOD_NAME);
			}
			else if (getPlayer().getHands().size() != StandardRuleConfig.INITIAL_HAND_COUNT)
			{
				throwException(new IllegalHandOperationException(
					getActivePlayerHand(),
					"Player cannot place an insurance side bet when they have already split."
				), METHOD_NAME);
			}
			else if (getPlayer().getChips().compareTo(getActivePlayerHand().getBet().getHalf()) < 0)
			{
				throwException(new InsufficientChipsException(
					getPlayer(),
					getActivePlayerHand().getBet().getHalf()
				), METHOD_NAME);
			}
			else
			{
				throwException(new RuleViolationException(RULE_VIOLATION_MESSAGE), METHOD_NAME);
			}
		}
		final var amount = getActivePlayerHand().getBet().getHalf();
		getPlayer().setChips(getPlayer().getChips().subtract(amount));
		final var insurancePot = new Pot(amount);
		final boolean wasSuccessful = getDealer().getHand().isBlackjack();
		var winnings = BigDecimal.ZERO;
		if (wasSuccessful)
		{
			winnings = insurancePot.scoop().multiply(
				StandardRuleConfig.INSURANCE.getPayoutMultiplier()
			);
			getPlayer().setChips(getPlayer().getChips().add(winnings));
			getListener().onInsuranceBetResolved(winnings);
			setState(EngineState.SHOWDOWN);
			showdown();
		}
		else
		{
			getListener().onInsuranceBetResolved(winnings);
			onDrawingRoundStartedPlayer();
			setState(EngineState.PLAYER_TURN);
		}
		getLogger().exiting(CLASS_NAME, METHOD_NAME);
	}

	public void deal() throws InsufficientChipsException
	{
		final var METHOD_NAME = "deal";
		getLogger().entering(CLASS_NAME, METHOD_NAME);
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
		if (getConfig().isInsuranceBetPossible(
			getActivePlayerHand(), getPlayer(), getDealer().getHand()))
		{
			getListener().onInsuranceBetOpportunityDetected();
			setState(EngineState.INSURANCE_CHECK);
		}
		else if (getActivePlayerHand().getHand().isBlackjack()
			|| getDealer().getHand().isBlackjack())
		{
			setState(EngineState.SHOWDOWN);
			showdown();
		}
		else
		{
			onDrawingRoundStartedPlayer();
			setState(EngineState.PLAYER_TURN);
		}
		getLogger().exiting(CLASS_NAME, METHOD_NAME);
	}

	private void dealCardForDealer()
	{
		final var METHOD_NAME = "dealCardForDealer";
		getLogger().entering(CLASS_NAME, METHOD_NAME);
		assert getState() == EngineState.DEALING || getState() == EngineState.DEALER_TURN
			: "getState() != EngineState.DEALING && getState() != EngineState.DEALER_TURN";
		final var card = getDealer().hit();
		getDealer().getHand().addCard(card);
		onCardDealtToDealer(card);
		getLogger().exiting(CLASS_NAME, METHOD_NAME, card);
	}

	private void dealCardForPlayer() throws InsufficientChipsException
	{
		final var METHOD_NAME = "dealCardForPlayer";
		getLogger().entering(CLASS_NAME, METHOD_NAME);
		assert getState() == EngineState.DEALING || getState() == EngineState.PLAYER_TURN
			: "getState() != EngineState.DEALING && getState() != EngineState.PLAYER_TURN";
		final var card = getDealer().hit();
		getActivePlayerHand().getHand().addCard(card);
		onCardDealtToPlayer(card);
		getLogger().exiting(CLASS_NAME, METHOD_NAME, card);
	}

	private void dealerTurn() throws InsufficientChipsException
	{
		final var METHOD_NAME = "dealerTurn";
		getLogger().entering(CLASS_NAME, METHOD_NAME);
		assert getActivePlayerHandIndex() == 0 :  "activeHandPlayerIndex != 0";
		assert getState() == EngineState.DEALER_TURN
			: "getState() != EngineState.DEALER_TURN";
		onDrawingRoundStartedDealer();
		while (getConfig().isDealerTurnActive(getState(), getDealer()))
		{
			dealCardForDealer();
		}
		onDrawingRoundCompletedDealer();
		getLogger().exiting(CLASS_NAME, METHOD_NAME);
	}

	public void declineInsuranceBet()
	{
		final var METHOD_NAME = "declineInsuranceBet";
		getLogger().entering(CLASS_NAME, METHOD_NAME);
		assert getActivePlayerHandIndex() == 0 :  "activeHandPlayerIndex != 0";
		assert getState() == EngineState.INSURANCE_CHECK
			: "getState() != EngineState.INSURANCE_CHECK";
		onDrawingRoundStartedPlayer();
		setState(EngineState.PLAYER_TURN);
		getLogger().exiting(CLASS_NAME, METHOD_NAME);
	}

	private void drawCardForPlayerAction() throws InsufficientChipsException
	{
		final var METHOD_NAME = "playerDrawCard";
		getLogger().entering(CLASS_NAME, METHOD_NAME);
		assert getState() == EngineState.PLAYER_TURN : "getState() != EngineState.PLAYER_TURN";
		dealCardForPlayer();
		if (getActivePlayerHand().getHand().isBusted())
		{
			onDrawingRoundCompletedPlayer();
		}
		getLogger().exiting(CLASS_NAME, METHOD_NAME);
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

	private void onBetPlaced()
	{
		final var METHOD_NAME = "onBetPlaced";
		getLogger().entering(CLASS_NAME, METHOD_NAME);
		assert getActivePlayerHandIndex() == 0 :  "activeHandPlayerIndex != 0";
		assert getState() == EngineState.BETTING : "getState() != EngineState.BETTING";
		getListener().onBetPlaced();
		getLogger().info(String.format(
			"Player %s has placed a bet of $%.2f on their %s hand.",
			getPlayer(),
			getActivePlayerHand().getBet().getAmount(),
			StringUtils.normalizeLower(getActivePlayerHand().getType().toString())
		));
		getLogger().exiting(CLASS_NAME, METHOD_NAME);
	}

	private void onCardDealtToDealer(Card card)
	{
		final var METHOD_NAME = "onCardDealtToDealer";
		getLogger().entering(CLASS_NAME, METHOD_NAME, card);
		assert card != null : "card == null";
		assert getState() == EngineState.DEALING || getState() == EngineState.DEALER_TURN
			: "getState() != EngineState.DEALING && getState() != EngineState.DEALER_TURN";
		getListener().onCardDealtToDealer(card);
		getLogger().info(String.format(
			"Added card %s to dealer's hand %s.",
			card, getDealer().getHand()
		));
		getLogger().exiting(CLASS_NAME, METHOD_NAME);
	}

	private void onCardDealtToPlayer(Card card) throws InsufficientChipsException
	{
		final var METHOD_NAME = "onCardDealtToPlayer";
		getLogger().entering(CLASS_NAME, METHOD_NAME, card);
		assert card != null : "card == null";
		assert getState() == EngineState.DEALING || getState() == EngineState.PLAYER_TURN
			: "getState() != EngineState.DEALING && getState() != EngineState.PLAYER_TURN";
		getListener().onCardDealtToPlayer(card);
		if (getState() == EngineState.PLAYER_TURN
			&& getActivePlayerHand().getHand().getCards().size() > StandardRuleConfig.INITIAL_CARD_COUNT)
		{
			getActivePlayerHand().markAsAltered();
		}
		getLogger().info(String.format(
			"Added card %s to player's hand %s.",
			card, getActivePlayerHand()
		));
		getLogger().exiting(CLASS_NAME, METHOD_NAME);
	}

	private void onDrawingRoundCompletedDealer() throws InsufficientChipsException
	{
		final var METHOD_NAME = "onDrawingRoundCompletedDealer";
		getLogger().entering(CLASS_NAME, METHOD_NAME);
		assert getActivePlayerHandIndex() == 0 :  "activeHandPlayerIndex != 0";
		assert getState() == EngineState.DEALER_TURN : "getState() != EngineState.DEALER_TURN";
		getListener().onDrawingRoundCompletedDealer();
		getLogger().info("The dealer's drawing round was completed.");
		setState(EngineState.SHOWDOWN);
		showdown();
		getLogger().exiting(CLASS_NAME, METHOD_NAME);
	}

	private void onDrawingRoundCompletedPlayer() throws InsufficientChipsException
	{
		final var METHOD_NAME = "onDrawingRoundCompletedPlayer";
		getLogger().entering(CLASS_NAME, METHOD_NAME);
		assert getState() == EngineState.PLAYER_TURN : "getState() != EngineState.PLAYER_TURN";
		getListener().onDrawingRoundCompletedPlayer();
		if (getActivePlayerHand().getType() == PlayerHandType.MAIN)
		{
			assert getActivePlayerHandIndex() == 0 : "activeHandPlayerIndex != 0";
			if (getActivePlayerHand().getHand().isBusted())
			{
				getLogger().info(String.format(
					"Player has busted with a score of %d on hand %s.",
					getActivePlayerHand().getHand().getScore(),
					getActivePlayerHand().getHand()
				));
				setState(EngineState.SHOWDOWN);
				showdown();
			}
			else if (getActivePlayerHand().getHasSurrendered())
			{
				getLogger().info(String.format(
					"Player has surrendered on hand %s.",
					getActivePlayerHand().getHand()
				));
				setState(EngineState.SHOWDOWN);
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
			onDrawingRoundStartedPlayer();
			setState(EngineState.PLAYER_TURN);
		}
		getLogger().info("The player's drawing round was completed.");
		getLogger().exiting(CLASS_NAME, METHOD_NAME);
	}

	private void onDrawingRoundStartedDealer()
	{
		final var METHOD_NAME = "onDrawingRoundStartedDealer";
		getLogger().entering(CLASS_NAME, METHOD_NAME);
		assert getActivePlayerHandIndex() == 0 :  "activeHandPlayerIndex != 0";
		assert getState() == EngineState.DEALER_TURN : "getState() != EngineState.DEALER_TURN";
		getListener().onDrawingRoundStartedDealer();
		getLogger().info("The dealer's drawing round was started.");
		getLogger().exiting(CLASS_NAME, METHOD_NAME);
	}

	private void onDrawingRoundStartedPlayer()
	{
		final var METHOD_NAME = "onDrawingRoundStartedPlayer";
		getLogger().entering(CLASS_NAME, METHOD_NAME);
		//assert getState() == EngineState.PLAYER_TURN : "getState() != EngineState.PLAYER_TURN";
		getListener().onDrawingRoundStartedPlayer();
		getLogger().info(String.format(
			"The player's drawing round was started on hand %s.",
			getActivePlayerHand()
		));
		getLogger().exiting(CLASS_NAME, METHOD_NAME);
	}

	public void placeHandBet(BigDecimal amount) throws Exception
	{
		final var METHOD_NAME = "placeBet";
		getLogger().entering(CLASS_NAME, METHOD_NAME, amount);
		assert amount != null && amount.compareTo(BigDecimal.ZERO) > 0
			: "amount == null || amount.compareTo(BigDecimal.ZERO) <= 0";
		assert getActivePlayerHandIndex() == 0 :  "activeHandPlayerIndex != 0";
		assert getState() == EngineState.BETTING : "getState() != EngineState.BETTING";
		if (getPlayer().getChips().compareTo(amount) < 0)
		{
			throwException(new InsufficientChipsException(
				getPlayer(),
				amount
			), METHOD_NAME);
		}
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
		getLogger().exiting(CLASS_NAME, METHOD_NAME, playerMainHand);
		deal();
	}

	public void playerDoubleDown() throws Exception
	{
		final var METHOD_NAME = "playerDoubleDown";
		getLogger().entering(CLASS_NAME, METHOD_NAME);
		assert getState() == EngineState.PLAYER_TURN : "getState() != EngineState.PLAYER_TURN";
		final var playerHand = getActivePlayerHand();
		if (!getConfig().isDoubleDownPossible(getActivePlayerHand(), getPlayer()))
		{
			if (getActivePlayerHand().isAltered())
			{
				throwException(new IllegalHandOperationException(
					playerHand,
					String.format(
						"Player cannot double down on a hand with more than %d cards",
						StandardRuleConfig.INITIAL_CARD_COUNT
					)
				), METHOD_NAME);
			}
			else if (getPlayer().getChips().compareTo(getActivePlayerHand().getBet().getAmount()) < 0)
			{
				throwException(new InsufficientChipsException(
					getPlayer(),
					getActivePlayerHand().getBet().getAmount()
				), METHOD_NAME);
			}
			else
			{
				throwException(new RuleViolationException(RULE_VIOLATION_MESSAGE), METHOD_NAME);
			}
		}
		final var doubleDownAmount = getActivePlayerHand().getBet().getAmount();
		getPlayer().setChips(getPlayer().getChips().subtract(doubleDownAmount));
		playerHand.setBet(new Bet(doubleDownAmount.multiply(BigDecimal.TWO)));
		playerHand.getPot().addChips(doubleDownAmount.multiply(BigDecimal.TWO));
		drawCardForPlayerAction();
		getLogger().info(String.format(
			"Player has doubled down on hand %s.",
			playerHand.getHand()
		));
		if (getActivePlayerHand() != null)
		{
			onDrawingRoundCompletedPlayer();
		}
		getLogger().exiting(CLASS_NAME, METHOD_NAME);
	}

	public void playerHit() throws InsufficientChipsException
	{
		final var METHOD_NAME = "playerHit";
		getLogger().entering(CLASS_NAME, METHOD_NAME);
		assert getState() == EngineState.PLAYER_TURN : "getState() != EngineState.PLAYER_TURN";
		drawCardForPlayerAction();
		if (getActivePlayerHand() != null
			&& !getActivePlayerHand().getHand().isBusted())
		{
			setState(EngineState.PLAYER_TURN);
		}
		getLogger().exiting(CLASS_NAME, METHOD_NAME);
	}
	
	public void playerSplit() throws Exception
	{
		final var METHOD_NAME = "playerSplit";
		getLogger().entering(CLASS_NAME, METHOD_NAME);
		assert getActivePlayerHandIndex() == 0 :  "activeHandPlayerIndex != 0";
		assert getState() == EngineState.PLAYER_TURN : "getState() != EngineState.PLAYER_TURN";
		final var playerPreviousHand = getActivePlayerHand();
		if (!getConfig().isSplitPossible(getActivePlayerHand(), getActivePlayerHandIndex(),
			getPlayer()))
		{
			if (getActivePlayerHand().isAltered()
				|| !playerPreviousHand.getHand().isPocketPair())
			{
				throwException(new IllegalHandOperationException(
					playerPreviousHand,
					"An attempt to split a non-pocket pair occurred."
				), METHOD_NAME);
			}
			else if (getActivePlayerHandIndex() >= StandardRuleConfig.MAXIMUM_SPLIT_COUNT)
			{
				throwException(new IllegalHandOperationException(
					getActivePlayerHand(),
					String.format(
						"Player cannot have more than %d hands.",
						StandardRuleConfig.MAXIMUM_SPLIT_COUNT + 1
					)
				), METHOD_NAME);
			}
			else if (getPlayer().getChips().compareTo(getActivePlayerHand().getBet().getAmount()) < 0)
			{
				throwException(new InsufficientChipsException(
					getPlayer(),
					getActivePlayerHand().getBet().getAmount()
				), METHOD_NAME);
			}
			else
			{
				throwException(new RuleViolationException(RULE_VIOLATION_MESSAGE), METHOD_NAME);
			}
		}
		final var splitAmount = getActivePlayerHand().getBet().getAmount();
		getPlayer().setChips(getPlayer().getChips().subtract(splitAmount));
		final var playerSplitHand = new PlayerHand(
			new Bet(splitAmount),
			PlayerHandType.SPLIT
		);
		playerSplitHand.getHand().addCard(playerPreviousHand.getHand().getCards().getLast());
		getPlayer().addHand(playerSplitHand);
		playerPreviousHand.getHand().removeCard(StandardRuleConfig.INITIAL_CARD_COUNT - 1);
		playerPreviousHand.markAsAltered();
		playerPreviousHand.getHand().addCard(getDealer().hit());
		playerSplitHand.getHand().addCard(getDealer().hit());
		playerSplitHand.getPot().addChips(splitAmount.multiply(BigDecimal.TWO));
		setActivePlayerHandIndex(getActivePlayerHandIndex() + 1);
		getLogger().info(String.format(
			"Player has elected to split. Player now has a main hand of %s and a split hand of %s.",
			getPlayer().getHands().getFirst(),
			playerSplitHand
		));
		getListener().onPlayerSplit(playerPreviousHand, playerSplitHand);
		onDrawingRoundStartedPlayer();
		setState(EngineState.PLAYER_TURN);
		getLogger().exiting(CLASS_NAME, METHOD_NAME);
	}

	public void playerStand() throws InsufficientChipsException
	{
		final var METHOD_NAME = "playerStand";
		getLogger().entering(CLASS_NAME, METHOD_NAME);
		assert getState() == EngineState.PLAYER_TURN : "getState() != EngineState.PLAYER_TURN";
		getActivePlayerHand().markAsAltered();
		onDrawingRoundCompletedPlayer();
		getLogger().exiting(CLASS_NAME, METHOD_NAME);
	}

	public void playerSurrender() throws Exception
	{
		final var METHOD_NAME = "playerSurrender";
		getLogger().entering(CLASS_NAME, METHOD_NAME);
		assert getState() == EngineState.PLAYER_TURN : "getState() != EngineState.PLAYER_TURN";
		if (!getConfig().isSurrenderPossible(getActivePlayerHand(), getPlayer()))
		{
			if (getActivePlayerHand().isAltered())
			{
				throwException(new IllegalHandOperationException(
					getActivePlayerHand(),
					String.format(
						"Player cannot surrender on a hand with more than %d cards",
						StandardRuleConfig.INITIAL_CARD_COUNT
					)
				), METHOD_NAME);
			}
			else
			{
				throwException(new RuleViolationException(RULE_VIOLATION_MESSAGE), METHOD_NAME);
			}
		}
		getActivePlayerHand().setHasSurrendered(true);
		onDrawingRoundCompletedPlayer();
		getLogger().exiting(CLASS_NAME, METHOD_NAME);
	}

	private void reset()
	{
		final var METHOD_NAME = "reset";
		getLogger().entering(CLASS_NAME, METHOD_NAME);
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
			getLogger().info("The player has busted.");
			getListener().onGameCompleted();
			setState(EngineState.END);
		}
		getLogger().exiting(CLASS_NAME, METHOD_NAME);
	}

	private void setActivePlayerHandIndex(int handIndex)
	{
		final var METHOD_NAME = "setActivePlayerHandIndex";
		getLogger().entering(CLASS_NAME, METHOD_NAME, handIndex);
		assert getState() == EngineState.PLAYER_TURN
			: "getState() != EngineState.PLAYER_TURN";
		assert handIndex >= 0 && handIndex < getPlayer().getHands().size()
			: "handIndex < 0 && handIndex >= getPlayer().getHands().size()";
		activeHandPlayerIndex = handIndex;
		getLogger().exiting(CLASS_NAME, METHOD_NAME);
	}

	private void setState(EngineState state)
	{
		final var METHOD_NAME = "setState";
		getLogger().entering(CLASS_NAME, METHOD_NAME, state);
		assert state != null : "state == null";
		final var oldState = getState();
		this.state = state;
		getListener().onStateChanged(oldState);
		getLogger().exiting(CLASS_NAME, METHOD_NAME);
	}

	private void showdown() throws InsufficientChipsException
	{
		final var METHOD_NAME = "showdown";
		getLogger().entering(CLASS_NAME, METHOD_NAME);
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
			getListener().onShowdownCompleted(playerBeatDealer, playerWinnings);
		}
		getListener().onBettingRoundCompleted();
		setState(EngineState.RESETTING);
		reset();
		getLogger().exiting(CLASS_NAME, METHOD_NAME);
	}

	public void start()
	{
		assert getState() == null || getState() == EngineState.START
			: "getState() != null && getState() != EngineState.START";
		getListener().onGameStarted();
		setState(EngineState.BETTING);
	}

	private void throwException(Exception e, String sourceMethod) throws Exception
	{
		assert e != null : "e == null";
		assert sourceMethod != null && !sourceMethod.isBlank()
			: "sourceMethod == null || sourceMethod.isBlank() ";
		getLogger().throwing(CLASS_NAME, sourceMethod, e);
		throw e;
	}

	@Override
	public String toString()
	{
		return String.format(
			"%s[config=%s,dealer=%s,logger=%s,player=%s,state=%s]",
			getClass().getName(),
			getConfig(),
			getDealer(),
			getLogger(),
			getPlayer(),
			getState()
		);
	}
}