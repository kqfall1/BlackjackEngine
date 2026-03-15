package com.github.kqfall1.java.blackjackEngine.model.engine;

import com.github.kqfall1.java.blackjackEngine.model.betting.*;
import com.github.kqfall1.java.blackjackEngine.model.cards.Card;
import com.github.kqfall1.java.blackjackEngine.model.cards.Deck;
import com.github.kqfall1.java.blackjackEngine.model.cards.Shoe;
import com.github.kqfall1.java.blackjackEngine.model.entities.*;
import com.github.kqfall1.java.blackjackEngine.model.enums.BlackjackEngineState;
import com.github.kqfall1.java.blackjackEngine.model.enums.HandContextType;
import com.github.kqfall1.java.blackjackEngine.model.enums.Rank;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.*;
import com.github.kqfall1.java.blackjackEngine.model.hands.*;
import com.github.kqfall1.java.blackjackEngine.model.interfaces.BlackjackEngineListener;
import com.github.kqfall1.java.blackjackEngine.model.interfaces.BlackjackRuleset;
import com.github.kqfall1.java.utils.LoggerUtils;
import com.github.kqfall1.java.utils.StringUtils;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Orchestrates the entire blackjack library by continuously processing input and
 * executing the main game loop.
 *
 * <p>Centralizes all core logic that influences gameplay as a public API, emits all internal
 * events through {@code BlackjackEngineListener} hooks, and logs pertinent information.</p>
 *
 * <p>Auto-advances past {@code BlackjackEngineState.BETTING} and {@code BlackjackEngineState.PLAYER_TURN} when
 * appropriate; one should call the advance instance methods of this {@code BlackjackEngine} to
 * advance past all other {@code BlackjackEngineState} states.</p>
 *
 * @author kqfall1
 * @since 15/12/2025
 */
public class BlackjackEngine
{
	/**
 	 * Identifies the {@code HandContext} object in the encapsulated {@code Player} object's
	 * {@code contexts} property that is currently being acted upon by this {@code BlackjackEngine}
	 * in a blackjack betting round.
	 *
	 * <p>
	 * During the {@code BlackjackEngineState.PLAYER_TURN} state, it indicates the {@code HandContext} that
	 * the {@code Player} is actively making decisions on; during other states, it remains at 0.
	 * </p>
 	 */
	private int activeHandContextIndex;
	public static final String CLASS_NAME = "BlackjackEngine";
	private final Dealer DEALER;
	private final BlackjackEngineListener LISTENER;
	private Logger logger;
	private final Player PLAYER;
	private final BlackjackRuleset RULESET;
	private static final String RULE_VIOLATION_MESSAGE = "A blackjack rule was violated.";
	private BlackjackEngineState state;

	public BlackjackEngine(BlackjackEngineListener listener, String loggerFilePath, String loggerName, BlackjackRuleset ruleset)
	{
		assert listener != null : "listener == null";
		assert loggerFilePath != null : "loggerFilePath == null";
		assert loggerName != null : "loggerName == null";
		assert ruleset != null : "ruleset == null";
		this.RULESET = ruleset;
		DEALER = new Dealer(
			ruleset.getIncludedRanks(),
			ruleset.getConfig().getShoeDeckCount(),
			ruleset.getConfig().getShoePenetration()
		);
		this.LISTENER = listener;
		if (ruleset.getConfig().isLoggingEnabled())
		{
			try
			{
				logger = LoggerUtils.newFileLogger(loggerFilePath, loggerName, true);
			}
			catch (IOException e)
			{
				logger = Logger.getLogger(loggerName);
			}
		}
		else
		{
			logger = Logger.getLogger(loggerName);
			logger.setLevel(Level.OFF);
		}
		PLAYER = new Player();
		getPlayer().setChips(ruleset.getConfig().getPlayerInitialChips());
		state = BlackjackEngineState.START;
	}

	public BigDecimal acceptInsuranceBet()
	{
		final var METHOD_NAME = "acceptInsuranceBet";
		getLogger().entering(CLASS_NAME, METHOD_NAME);
		assert getActiveHandContextIndex() == HandContextType.MAIN.ordinal() : "activeHandContextIndex != HandContextType.MAIN.ordinal()";
		assert getState() == BlackjackEngineState.INSURANCE_CHECK : "getState() != BlackjackEngineState.INSURANCE_CHECK";
		if (!RULESET.isInsuranceBetPossible(
			getActiveHandContext(), getState(), getPlayer(), getDealer().getHand()))
		{
			if (getActiveHandContext().isAltered())
			{
			  	throwException(new IllegalHandOperationException(
					getActiveHandContext(),
					String.format(
						"Player cannot place an insurance side bet with a hand with more than %d cards.",
						BlackjackConstants.INITIAL_CARD_COUNT
					)
				), METHOD_NAME);
			}
			else if (getDealer().getHand().getCards().getFirst().getRank() != Rank.ACE)
			{
				throwException(new IllegalHandOperationException(
					getActiveHandContext(),
					"Player cannot place an insurance side bet when the dealer's up card isn't an ace."
				), METHOD_NAME);
			}
			else if (getPlayer().getContexts().size() != BlackjackConstants.INITIAL_HAND_COUNT)
			{
				throwException(new IllegalHandOperationException(
					getActiveHandContext(),
					"Player cannot place an insurance side bet when they have already split."
				), METHOD_NAME);
			}
			else if (getPlayer().getChips().compareTo(getActiveHandContext().getBet().getHalf()) < 0)
			{
				throwException(new InsufficientChipsException(
					getPlayer(),
					getActiveHandContext().getBet().getHalf()
				), METHOD_NAME);
			}
			else
			{
				throwException(new RuleViolationException(RULE_VIOLATION_MESSAGE), METHOD_NAME);
			}
		}
		final var AMOUNT = getActiveHandContext().getBet().getHalf();
		var playerWinnings = BigDecimal.ZERO;
		getPlayer().setChips(getPlayer().getChips().subtract(AMOUNT));
		final var INSURANCE_POT = new Pot(AMOUNT);
		final var INSURANCE_RATIO = RULESET
			.getPayoutRatios()
			.get(BlackjackConstants.INSURANCE_RATIO_KEY);
		if (RULESET.isHandBlackjack(getDealer().getHand()))
		{
			playerWinnings = INSURANCE_POT.scoop().multiply(INSURANCE_RATIO.getPayoutMultiplier());
			getPlayer().setChips(getPlayer().getChips().add(playerWinnings));
		}
		getLogger().exiting(CLASS_NAME, METHOD_NAME);
		return playerWinnings;
	}

	public void advanceAfterDeal()
	{
		final var METHOD_NAME = "advanceAfterDeal";
		getLogger().entering(CLASS_NAME, METHOD_NAME);
		assert getActiveHandContextIndex() == HandContextType.MAIN.ordinal() : "activeHandContextIndex != HandContextType.MAIN.ordinal()";
		assert getState() == BlackjackEngineState.DEALING : "getState() != BlackjackEngineState.DEALING";
		if (RULESET.isInsuranceBetPossible(
			getActiveHandContext(), getState(), getPlayer(), getDealer().getHand()))
		{
			getListener().onInsuranceBetOpportunityDetected(getDealer().getHand().getCards().getFirst());
			setState(BlackjackEngineState.INSURANCE_CHECK);
		}
		else if (RULESET.isHandBlackjack(getActiveHandContext().getHand())
			|| (RULESET.isHandBlackjack(getDealer().getHand())
				&& RULESET.shouldDealerPeekForBlackjack()))
		{
			setState(BlackjackEngineState.SHOWDOWN);
		}
		else
		{
			onDrawingRoundStartedPlayer();
		}
		getLogger().exiting(CLASS_NAME, METHOD_NAME);
	}

	public void advanceAfterDealerTurn()
	{
		final var METHOD_NAME = "advanceAfterDealerTurn";
		getLogger().entering(CLASS_NAME, METHOD_NAME);
		assert getActiveHandContextIndex() == getPlayer().getContexts().size() - 1 : "getActiveHandContextIndex() != getPlayer().getContexts().size() - 1";
		assert getState() == BlackjackEngineState.DEALER_TURN : "getState() != BlackjackEngineState.DEALER_TURN";
		setState(BlackjackEngineState.SHOWDOWN);
		getLogger().exiting(CLASS_NAME, METHOD_NAME);
	}

	public void advanceAfterDrawingRoundCompletedPlayer()
	{
		if (getActiveHandContextIndex() < getPlayer().getContexts().size() - 1)
		{
			setActiveHandContextIndex(getActiveHandContextIndex() + 1);
			onDrawingRoundStartedPlayer();
		}
		else
		{
			advanceAfterPlayerTurn();
		}
	}

	public void advanceAfterInsuranceBet(BigDecimal winnings)
	{
		final var METHOD_NAME = "advanceAfterInsuranceBet";
		getLogger().entering(CLASS_NAME, METHOD_NAME);
		assert getActiveHandContextIndex() == HandContextType.MAIN.ordinal() : "activeHandContextIndex != HandContextType.MAIN.ordinal()";
		assert getState() == BlackjackEngineState.INSURANCE_CHECK : "getState() != BlackjackEngineState.INSURANCE_CHECK";
		if (RULESET.isHandBlackjack(getDealer().getHand()))
		{

			getListener().onInsuranceBetResolved(true, winnings);
			setState(BlackjackEngineState.SHOWDOWN);
		}
		else
		{
			getListener().onInsuranceBetResolved(false, winnings);
			onDrawingRoundStartedPlayer();
		}
		getLogger().exiting(CLASS_NAME, METHOD_NAME);
	}

	public void advanceAfterPlayerTurn()
	{
		final var METHOD_NAME = "advanceAfterPlayerTurn";
		getLogger().entering(CLASS_NAME, METHOD_NAME);
		assert getActiveHandContextIndex() == getPlayer().getContexts().size() - 1 : "getActiveHandContextIndex() != getPlayer().getContexts().size() - 1";
		assert getState() == BlackjackEngineState.PLAYER_TURN : "getState() != BlackjackEngineState.PLAYER_TURN";
		if (RULESET.isHandBusted(getActiveHandContext().getHand()))
		{
			getLogger().info(String.format(
				"Player has busted with a score of %d on hand %s.",
				getActiveHandContext().getHand().getScore(),
				getActiveHandContext().getHand()
			));
			setState(BlackjackEngineState.SHOWDOWN);
		}
		else if (getActiveHandContext().hasSurrendered())
		{
			getLogger().info(String.format(
				"Player has surrendered on hand %s.",
				getActiveHandContext().getHand()
			));
			setState(BlackjackEngineState.SHOWDOWN);
		}
		else
		{
			setState(BlackjackEngineState.DEALER_TURN);
		}
		getLogger().exiting(CLASS_NAME, METHOD_NAME);
	}

	public void advanceAfterReset()
	{
		final var METHOD_NAME = "advanceAfterReset";
		getLogger().entering(CLASS_NAME, METHOD_NAME);
		assert getState() == BlackjackEngineState.RESETTING : "getState() != BlackjackEngineState.RESETTING";
		if (getPlayer().getChips().compareTo(RULESET.getConfig().getMinimumBetAmount()) >= 0)
		{
			setState(BlackjackEngineState.BETTING);
		}
		else
		{
			getLogger().info("The player has busted.");
			getListener().onGameCompleted();
			setState(BlackjackEngineState.END);
		}
	}

	public void advanceAfterShowdown()
	{
		final var METHOD_NAME = "advanceAfterShowdown";
		getLogger().entering(CLASS_NAME, METHOD_NAME);
		assert getActiveHandContextIndex() == getPlayer().getContexts().size() - 1 : "getActiveHandContextIndex() != getPlayer().getContexts().size() - 1";
		assert getState() == BlackjackEngineState.SHOWDOWN : "getState() != BlackjackEngineState.SHOWDOWN";
		setState(BlackjackEngineState.RESETTING);
		getLogger().exiting(CLASS_NAME, METHOD_NAME);
	}

	public void deal()
	{
		final var METHOD_NAME = "deal";
		getLogger().entering(CLASS_NAME, METHOD_NAME);
		assert getActiveHandContextIndex() == HandContextType.MAIN.ordinal() : "activeHandContextIndex != HandContextType.MAIN.ordinal()";
		assert getState() == BlackjackEngineState.DEALING : "getState() != BlackjackEngineState.DEALING";
		final var dealerHand = getDealer().getHand();
		assert dealerHand.getCards().isEmpty() : "!dealerHand.getCards().isEmpty()";
		assert getActiveHandContext().getHand().getCards().isEmpty()
			: "!getActiveHandContext().getHand().getCards().isEmpty()";
		for (int count = 0; count < BlackjackConstants.INITIAL_CARD_COUNT; count++)
		{
			dealCardForPlayer();
			dealCardForDealer();
		}
		assert getActiveHandContext().getHand().getCards().size() == BlackjackConstants.INITIAL_CARD_COUNT
			: "getActiveHandContext().getHand().getCards().size() != RuleConfig.INITIAL_CARD_COUNT";
		assert dealerHand.getCards().size() == BlackjackConstants.INITIAL_CARD_COUNT
			: "dealerHand.getCards().size() != RuleConfig.INITIAL_CARD_COUNT";
		getLogger().info(String.format(
			"The cards have been dealt. Player's hand: %s. The dealer's up card is %s.",
			getActiveHandContext(),
			dealerHand.getCards().get(BlackjackConstants.INITIAL_CARD_COUNT - 1)
		));
		getLogger().exiting(CLASS_NAME, METHOD_NAME);
	}

	private void dealCardForDealer()
	{
		final var METHOD_NAME = "dealCardForDealer";
		getLogger().entering(CLASS_NAME, METHOD_NAME);
		assert getState() == BlackjackEngineState.DEALING || getState() == BlackjackEngineState.DEALER_TURN : "getState() != BlackjackEngineState.DEALING && getState() != BlackjackEngineState.DEALER_TURN";
		Card card;
		try
		{
			card = getDealer().getCardSource().draw();
		}
		catch (NoMoreCardsException e)
		{
			getDealer().setCardSource(new Deck());
			card = getDealer().getCardSource().draw();
		}
		getDealer().getHand().addCards(card);
		onCardDealtToDealer(card);
		getLogger().exiting(CLASS_NAME, METHOD_NAME, card);
	}

	private void dealCardForPlayer()
	{
		final var METHOD_NAME = "dealCardForPlayer";
		getLogger().entering(CLASS_NAME, METHOD_NAME);
		assert getState() == BlackjackEngineState.DEALING || getState() == BlackjackEngineState.PLAYER_TURN : "getState() != BlackjackEngineState.DEALING && getState() != BlackjackEngineState.PLAYER_TURN";
		Card card;
		try
		{
			card = getDealer().getCardSource().draw();
		}
		catch (NoMoreCardsException e)
		{
			getDealer().setCardSource(new Deck());
			card = getDealer().getCardSource().draw();
		}
		getActiveHandContext().getHand().addCards(card);
		onCardDealtToPlayer(card);
		getLogger().exiting(CLASS_NAME, METHOD_NAME, card);
	}

	public void dealerTurn()
	{
		final var METHOD_NAME = "dealer";
		getLogger().entering(CLASS_NAME, METHOD_NAME);
		assert getActiveHandContextIndex() == getPlayer().getContexts().size() - 1 : "getActiveHandContextIndex() != getPlayer().getContexts().size() - 1";
		assert getState() == BlackjackEngineState.DEALER_TURN : "getState() != BlackjackEngineState.DEALER_TURN";
		onDrawingRoundStartedDealer();
		while (RULESET.isDealerTurnActive(getState(), getDealer()))
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
		assert getActiveHandContextIndex() == HandContextType.MAIN.ordinal() : "activeHandContextIndex != HandContextType.MAIN.ordinal()";
		assert getState() == BlackjackEngineState.INSURANCE_CHECK
			: "getState() != BlackjackEngineState.INSURANCE_CHECK";
		if (RULESET.isHandBlackjack(getDealer().getHand())
			|| RULESET.isHandBlackjack(getActiveHandContext().getHand()))
		{
			setState(BlackjackEngineState.SHOWDOWN);
		}
		else
		{
			onDrawingRoundStartedPlayer();
		}
		getLogger().exiting(CLASS_NAME, METHOD_NAME);
	}

	private void drawCardForPlayerAction()
	{
		final var METHOD_NAME = "playerDrawCard";
		getLogger().entering(CLASS_NAME, METHOD_NAME);
		assert getState() == BlackjackEngineState.PLAYER_TURN : "getState() != BlackjackEngineState.PLAYER_TURN";
		dealCardForPlayer();
		if (RULESET.isHandBusted(getActiveHandContext().getHand()))
		{
			onDrawingRoundCompletedPlayer();
		}
		getLogger().exiting(CLASS_NAME, METHOD_NAME);
	}

	/**
	 * Retrieves the {@code HandContext} object corresponding to {@code activeHandContextIndex}.
	 *
	 * <p>
	 * This method is safe to call in all {@code BlackjackEngineState} states as long as the
	 * {@code Player} possesses at least one non-null {@code HandContext}.
	 * </p>
 	 */
	public HandContext getActiveHandContext()
	{
		return getPlayer().getContexts().get(getActiveHandContextIndex());
	}

	public int getActiveHandContextIndex()
	{
		return activeHandContextIndex;
	}

	public Dealer getDealer()
	{
		return DEALER;
	}

	public BlackjackEngineListener getListener()
	{
		return LISTENER;
	}

	public Logger getLogger()
	{
		return logger;
	}

	public Player getPlayer()
	{
		return PLAYER;
	}

	public BlackjackRuleset getRuleset()
	{
		return RULESET;
	}

	public BlackjackEngineState getState()
	{
		return state;
	}

	private void onBetPlaced()
	{
		final var METHOD_NAME = "onBetPlaced";
		getLogger().entering(CLASS_NAME, METHOD_NAME);
		assert getActiveHandContextIndex() == HandContextType.MAIN.ordinal() : "activeHandContextIndex != HandContextType.MAIN.ordinal()";
		assert getState() == BlackjackEngineState.BETTING : "getState() != BlackjackEngineState.BETTING";
		getListener().onBetPlaced(getActiveHandContext());
		getLogger().info(String.format(
			"Player %s has placed a bet of $%.2f on their %s hand.",
			getPlayer(),
			getActiveHandContext().getBet().getAmount(),
			StringUtils.normalizeLower(getActiveHandContext().getType().toString())
		));
		getLogger().exiting(CLASS_NAME, METHOD_NAME);
	}

	private void onCardDealtToDealer(Card card)
	{
		final var METHOD_NAME = "onCardDealtToDealer";
		getLogger().entering(CLASS_NAME, METHOD_NAME, card);
		assert card != null : "card == null";
		assert getState() == BlackjackEngineState.DEALING || getState() == BlackjackEngineState.DEALER_TURN
			: "getState() != BlackjackEngineState.DEALING && getState() != BlackjackEngineState.DEALER_TURN";
		final var isFaceUpCard = getDealer().getHand().getCards().size() != BlackjackConstants.INITIAL_CARD_COUNT
			|| !RULESET.isDealerSecondCardFaceDown();
			getListener().onCardDealtToDealer(card, getDealer().getHand(), isFaceUpCard);
			getLogger().info(String.format(
				"Added card %s to dealer's hand %s.",
				card, getDealer().getHand()
			));
		getLogger().exiting(CLASS_NAME, METHOD_NAME);
	}

	private void onCardDealtToPlayer(Card card)
	{
		final var METHOD_NAME = "onCardDealtToPlayer";
		getLogger().entering(CLASS_NAME, METHOD_NAME, card);
		assert card != null : "card == null";
		assert getState() == BlackjackEngineState.DEALING || getState() == BlackjackEngineState.PLAYER_TURN
			: "getState() != BlackjackEngineState.DEALING && getState() != BlackjackEngineState.PLAYER_TURN";
		if (getState() == BlackjackEngineState.PLAYER_TURN
			&& getActiveHandContext().getHand().getCards().size() > BlackjackConstants.INITIAL_CARD_COUNT)
		{
			getActiveHandContext().markAsAltered();
		}
		getListener().onCardDealtToPlayer(card, getActiveHandContext());
		getLogger().info(String.format(
			"Added card %s to player's hand %s.",
			card, getActiveHandContext()
		));
		getLogger().exiting(CLASS_NAME, METHOD_NAME);
	}

	private void onDrawingRoundCompletedDealer()
	{
		final var METHOD_NAME = "onDrawingRoundCompletedDealer";
		getLogger().entering(CLASS_NAME, METHOD_NAME);
		assert getActiveHandContextIndex() == getPlayer().getContexts().size() - 1 : "getActiveHandContextIndex() != getPlayer().getContexts().size() - 1";
		assert getState() == BlackjackEngineState.DEALER_TURN : "getState() != BlackjackEngineState.DEALER_TURN";
		getListener().onDrawingRoundCompletedDealer(getDealer().getHand());
		getLogger().info("The dealer's drawing round was completed.");
		getLogger().exiting(CLASS_NAME, METHOD_NAME);
	}

	private void onDrawingRoundCompletedPlayer()
	{
		final var METHOD_NAME = "onDrawingRoundCompletedPlayer";
		getLogger().entering(CLASS_NAME, METHOD_NAME);
		assert getState() == BlackjackEngineState.PLAYER_TURN : "getState() != BlackjackEngineState.PLAYER_TURN";
		getListener().onDrawingRoundCompletedPlayer(getActiveHandContext());
		getLogger().info("The player's drawing round was completed.");
		getLogger().exiting(CLASS_NAME, METHOD_NAME);
	}

	private void onDrawingRoundStartedDealer()
	{
		final var METHOD_NAME = "onDrawingRoundStartedDealer";
		getLogger().entering(CLASS_NAME, METHOD_NAME);
		assert getActiveHandContextIndex() == getPlayer().getContexts().size() - 1 : "getActiveHandContextIndex() != getPlayer().getContexts().size() - 1";
		assert getState() == BlackjackEngineState.DEALER_TURN : "getState() != BlackjackEngineState.DEALER_TURN";
		getListener().onDrawingRoundStartedDealer(getDealer().getHand());
		getLogger().info("The dealer's drawing round was started.");
		getLogger().exiting(CLASS_NAME, METHOD_NAME);
	}

	private void onDrawingRoundStartedPlayer()
	{
		final var METHOD_NAME = "onDrawingRoundStartedPlayer";
		getLogger().entering(CLASS_NAME, METHOD_NAME);
		getListener().onDrawingRoundStartedPlayer(getActiveHandContext());
		getLogger().info(String.format(
			"The player's drawing round was started on hand %s.",
			getActiveHandContext()
		));
		setState(BlackjackEngineState.PLAYER_TURN);
		getLogger().exiting(CLASS_NAME, METHOD_NAME);
	}

	public void placeBet(BigDecimal amount)
	{
		final var METHOD_NAME = "placeBet";
		getLogger().entering(CLASS_NAME, METHOD_NAME, amount);
		assert amount != null && amount.compareTo(BigDecimal.ZERO) > 0
			: "amount == null || amount.compareTo(BigDecimal.ZERO) <= 0";
		assert getActiveHandContextIndex() == HandContextType.MAIN.ordinal() : "activeHandContextIndex != HandContextType.MAIN.ordinal()";
		assert getState() == BlackjackEngineState.BETTING : "getState() != BlackjackEngineState.BETTING";
		if (getPlayer().getChips().compareTo(amount) < 0)
		{
			throwException(new InsufficientChipsException(
				getPlayer(),
				amount
			), METHOD_NAME);
		}
		else if (RULESET.getConfig().getMinimumBetAmount().compareTo(amount) > 0)
		{
			throw new RuleViolationException(String.format(
				"Cannot place a bet lower than $%.2f.",
				RULESET.getConfig().getMinimumBetAmount()
			));
		}
		getListener().onBettingRoundStarted();
		final var playerMainHand = new HandContext(
			new Bet(amount),
			HandContextType.MAIN
		);
		getPlayer().addContext(playerMainHand);
		getPlayer().setChips(getPlayer().getChips().subtract(amount));
		getActiveHandContext().getPot().addChips(amount.multiply(BigDecimal.TWO));
		onBetPlaced();
		setState(BlackjackEngineState.DEALING);
		getLogger().exiting(CLASS_NAME, METHOD_NAME, playerMainHand);
	}

	public void playerDoubleDown()
	{
		final var METHOD_NAME = "playerDoubleDown";
		getLogger().entering(CLASS_NAME, METHOD_NAME);
		assert getState() == BlackjackEngineState.PLAYER_TURN : "getState() != BlackjackEngineState.PLAYER_TURN";
		final var context = getActiveHandContext();
		if (!RULESET.isDoubleDownPossible(getActiveHandContext(), getState(), getPlayer()))
		{
			if (getActiveHandContext().isAltered())
			{
				throwException(new IllegalHandOperationException(
					context,
					"Player cannot double down on an already altered hand"
				), METHOD_NAME);
			}
			else if (getPlayer().getChips().compareTo(getActiveHandContext().getBet().getAmount()) < 0)
			{
				throwException(new InsufficientChipsException(
					getPlayer(),
					getActiveHandContext().getBet().getAmount()
				), METHOD_NAME);
			}
			else
			{
				throwException(new RuleViolationException(RULE_VIOLATION_MESSAGE), METHOD_NAME);
			}
		}
		final var doubleDownAmount = getActiveHandContext().getBet().getAmount();
		getPlayer().setChips(getPlayer().getChips().subtract(doubleDownAmount));
		context.setBet(new Bet(doubleDownAmount.multiply(BigDecimal.TWO)));
		context.getPot().addChips(doubleDownAmount.multiply(BigDecimal.TWO));
		drawCardForPlayerAction();
		getLogger().info(String.format(
			"Player has doubled down on hand %s.",
			context.getHand()
		));
		if (getState() == BlackjackEngineState.PLAYER_TURN
			&& !RULESET.isHandBusted(context.getHand()))
		{
			onDrawingRoundCompletedPlayer();
		}
		getLogger().exiting(CLASS_NAME, METHOD_NAME);
	}

	public void playerHit()
	{
		final var METHOD_NAME = "playerHit";
		getLogger().entering(CLASS_NAME, METHOD_NAME);
		assert getState() == BlackjackEngineState.PLAYER_TURN : "getState() != BlackjackEngineState.PLAYER_TURN";
		drawCardForPlayerAction();
		if (getState() == BlackjackEngineState.PLAYER_TURN
			&& !RULESET.isHandBusted(getActiveHandContext().getHand()))
		{
			setState(BlackjackEngineState.PLAYER_TURN);
		}
		getLogger().exiting(CLASS_NAME, METHOD_NAME);
	}
	
	public void playerSplit()
	{
		final var METHOD_NAME = "playerSplit";
		getLogger().entering(CLASS_NAME, METHOD_NAME);
		assert getState() == BlackjackEngineState.PLAYER_TURN : "getState() != BlackjackEngineState.PLAYER_TURN";
		if (!RULESET.isSplitPossible(getActiveHandContext(), getState(),
			getActiveHandContextIndex(), getPlayer()))
		{
			if (getActiveHandContext().isAltered()
				|| !getActiveHandContext().getHand().isPocketPair())
			{
				throwException(new IllegalHandOperationException(
					getActiveHandContext(),
					"An attempt to split a non-pocket pair occurred."
				), METHOD_NAME);
			}
			else if (getActiveHandContextIndex() >= RULESET.getConfig().getMaximumSplitCount() + 1)
			{
				throwException(new IllegalHandOperationException(
					getActiveHandContext(),
					String.format(
						"Player cannot have more than %d hands.",
						RULESET.getConfig().getMaximumSplitCount() + 1
					)
				), METHOD_NAME);
			}
			else if (getPlayer().getChips().compareTo(getActiveHandContext().getBet().getAmount()) < 0)
			{
				throwException(new InsufficientChipsException(
					getPlayer(),
					getActiveHandContext().getBet().getAmount()
				), METHOD_NAME);
			}
			else
			{
				throwException(new RuleViolationException(RULE_VIOLATION_MESSAGE), METHOD_NAME);
			}
		}
		final var splitAmount = getActiveHandContext().getBet().getAmount();
		getPlayer().setChips(getPlayer().getChips().subtract(splitAmount));
		final var playerSplitHand = new HandContext(
			new Bet(splitAmount),
			HandContextType.SPLIT
		);
		playerSplitHand.getHand().addCards(getActiveHandContext().getHand().getCards().getLast());
		getPlayer().addContext(playerSplitHand);
		getActiveHandContext().getHand().removeCard(BlackjackConstants.INITIAL_CARD_COUNT - 1);
		getActiveHandContext().getHand().addCards(getDealer().getCardSource().draw());
		playerSplitHand.getHand().addCards(getDealer().getCardSource().draw());
		playerSplitHand.getPot().addChips(splitAmount.multiply(BigDecimal.TWO));
		getLogger().info(String.format(
			"Player has elected to split. Player now has a current hand of %s and a split hand of %s.",
			getPlayer().getContexts().getFirst(),
			playerSplitHand
		));
		getListener().onPlayerSplit(getActiveHandContext(), playerSplitHand);
		onDrawingRoundStartedPlayer();
		getLogger().exiting(CLASS_NAME, METHOD_NAME);
	}

	public void playerStand()
	{
		final var METHOD_NAME = "playerStand";
		getLogger().entering(CLASS_NAME, METHOD_NAME);
		assert getState() == BlackjackEngineState.PLAYER_TURN : "getState() != BlackjackEngineState.PLAYER_TURN";
		getActiveHandContext().markAsAltered();
		onDrawingRoundCompletedPlayer();
		getLogger().exiting(CLASS_NAME, METHOD_NAME);
	}

	public void playerSurrender()
	{
		final var METHOD_NAME = "playerSurrender";
		getLogger().entering(CLASS_NAME, METHOD_NAME);
		assert getState() == BlackjackEngineState.PLAYER_TURN : "getState() != BlackjackEngineState.PLAYER_TURN";
		if (!RULESET.isSurrenderingPossible(getActiveHandContext(), getState()))
		{
			if (getActiveHandContext().isAltered())
			{
				throwException(new IllegalHandOperationException(
					getActiveHandContext(),
					String.format(
						"Player cannot surrender on a hand with more than %d cards",
						BlackjackConstants.INITIAL_CARD_COUNT
					)
				), METHOD_NAME);
			}
			else
			{
				throwException(new RuleViolationException(RULE_VIOLATION_MESSAGE), METHOD_NAME);
			}
		}
		getActiveHandContext().setHasSurrendered();
		onDrawingRoundCompletedPlayer();
		getLogger().exiting(CLASS_NAME, METHOD_NAME);
	}

	public void reset()
	{
		final var METHOD_NAME = "reset";
		getLogger().entering(CLASS_NAME, METHOD_NAME);
		assert getActiveHandContextIndex() == getPlayer().getContexts().size() - 1 : "getActiveHandContextIndex() != getPlayer().getContexts().size() - 1";
		assert getState() == BlackjackEngineState.RESETTING : "getState() != BlackjackEngineState.RESETTING";
		if ((getDealer().getCardSource() instanceof Shoe shoe
				&& shoe.getCards().size() <= shoe.getCutoffAmount())
			|| getDealer().getCardSource() instanceof Deck)
		{
			getDealer().setCardSource(
				new Shoe(
					RULESET.getIncludedRanks(),
					BlackjackConstants.DEFAULT_SHOE_DECK_COUNT,
					RULESET.getConfig().getShoePenetration()
				)
			);
		}
		getDealer().setHand(new Hand());
		setActiveHandContextIndex(0);
		getPlayer().clearContexts();
		getListener().onReset();
		getLogger().exiting(CLASS_NAME, METHOD_NAME);
	}

	private void setActiveHandContextIndex(int contextIndex)
	{
		final var METHOD_NAME = "setActiveHandContextIndex";
		getLogger().entering(CLASS_NAME, METHOD_NAME, contextIndex);
		assert getState() == BlackjackEngineState.PLAYER_TURN || getState() == BlackjackEngineState.RESETTING
			: "getState() != BlackjackEngineState.PLAYER_TURN && getState() != BlackjackEngineState.RESETTING";
		assert contextIndex >= 0 && contextIndex < getPlayer().getContexts().size() : "handIndex < 0 && handIndex >= getPlayer().getHands().size()";
		activeHandContextIndex = contextIndex;
		getLogger().exiting(CLASS_NAME, METHOD_NAME);
	}

	private void setState(BlackjackEngineState state)
	{
		final var METHOD_NAME = "setState";
		getLogger().entering(CLASS_NAME, METHOD_NAME, state);
		assert state != null : "state == null";
		final var oldState = getState();
		this.state = state;
		getListener().onStateChanged(oldState);
		getLogger().exiting(CLASS_NAME, METHOD_NAME);
	}

	public void showdown()
	{
		final var METHOD_NAME = "showdown";
		getLogger().entering(CLASS_NAME, METHOD_NAME);
		assert getActiveHandContextIndex() == getPlayer().getContexts().size() - 1 : "getActiveHandContextIndex() != getPlayer().getContexts().size() - 1";
		assert getState() == BlackjackEngineState.SHOWDOWN : "getState() != BlackjackEngineState.SHOWDOWN";
		final var PAYOUT_RATIOS = RULESET.getPayoutRatios();
		for (HandContext handContext : RULESET.getHandContextsInShowdownOrder(getPlayer()))
		{
			getListener().onShowdownStarted(getDealer().getHand(), handContext);
			var playerBeatDealer = false;
			var playerWinnings = BigDecimal.ZERO;
			if (handContext.hasSurrendered())
			{
				playerWinnings = handContext.getPot().scoop().multiply(
					PAYOUT_RATIOS
						.get(BlackjackConstants.SURRENDER_RATIO_KEY)
						.getPayoutMultiplier()
				).setScale(BlackjackConstants.DEFAULT_CHIP_SCALE, RoundingMode.HALF_DOWN);
			}
			else if (RULESET.isHandBusted(handContext.getHand()))
			{
				handContext.getPot().scoop();
			}
			else if (RULESET.isHandBusted(getDealer().getHand()))
			{
				playerBeatDealer = true;
				playerWinnings = handContext.getPot().scoop()
					.setScale(BlackjackConstants.DEFAULT_CHIP_SCALE, RoundingMode.HALF_DOWN);
			}
			else
			{
				if (handContext.getHand().getScore() == getDealer().getHand().getScore())
				{
					playerWinnings = handContext.getPot().scoop().multiply(
						PAYOUT_RATIOS
							.get(BlackjackConstants.PUSH_RATIO_KEY)
							.getPayoutMultiplier()
					).setScale(BlackjackConstants.DEFAULT_CHIP_SCALE, RoundingMode.HALF_DOWN);
				}
				else if (handContext.getHand().getScore() > getDealer().getHand().getScore())
				{
					playerBeatDealer = true;

					if (RULESET.isHandBlackjack(handContext.getHand()))
					{
						playerWinnings = handContext.getPot().scoop().multiply(
							PAYOUT_RATIOS
								.get(BlackjackConstants.BLACKJACK_RATIO_KEY)
								.getPayoutMultiplier()
						).setScale(BlackjackConstants.DEFAULT_CHIP_SCALE, RoundingMode.HALF_DOWN);
					}
					else
					{
						playerWinnings = handContext.getPot().scoop()
							.setScale(BlackjackConstants.DEFAULT_CHIP_SCALE, RoundingMode.HALF_DOWN);
					}
				}
			}
			getPlayer().setChips(getPlayer().getChips().add(playerWinnings));
			getLogger().info(String.format(
				"Player's hand %s was showed down against dealer's hand %s.",
				handContext, getDealer().getHand()
			));
			getListener().onShowdownCompleted(getDealer().getHand(), handContext,
				playerBeatDealer, playerWinnings);
		}
		getListener().onBettingRoundCompleted();
		getLogger().exiting(CLASS_NAME, METHOD_NAME);
	}

	public void start()
	{
		assert getState() == null || getState() == BlackjackEngineState.START
			: "getState() != null && getState() != BlackjackEngineState.START";
		getListener().onGameStarted();
		setState(BlackjackEngineState.BETTING);
	}

	private void throwException(RuntimeException e, String sourceMethod)
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
			"%s[dealer=%s,logger=%s,player=%s,ruleset=%s,state=%s]",
			getClass().getName(),
			getDealer(),
			getLogger(),
			getPlayer(),
		RULESET,
			getState()
		);
	}
}