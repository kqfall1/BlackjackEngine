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
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Optional;

/**
 * Orchestrates the entire blackjack library by continuously processing input and
 * executing the main game loop.
 *
 * <p>Centralizes all core logic that influences gameplay as a public API, emits all internal
 * events through {@code BlackjackEngineListener} callback methods, and logs pertinent information.</p>
 *
 * <p>Auto-advances past {@code BlackjackEngineState.BETTING} and {@code BlackjackEngineState.PLAYER_TURN} when
 * appropriate; one should call the advance instance methods of this {@code BlackjackEngine} to
 * advance past all other {@code BlackjackEngineState} states.</p>
 *
 * <p>Throws {@code AssertionError}, {@code IllegalHandOperationException}, {@code InsufficentChipsException},
 * and {@code RuleViolationException} objects if called in an invalid state.</p>
 *
 * @author kqfall1
 * @since 15/12/2025
 */
public class BlackjackEngine
{
	private int activeHandContextIndex;
	private final Dealer dealer;
	private final BlackjackEngineListener listener;
	private Logger logger;
	private final Player player;
	private final BlackjackRuleset ruleset;
	private Iterator<HandContext> showdownHandContextIterator;
	private BlackjackEngineState state;

	public BlackjackEngine(BlackjackEngineListener listener, Optional<Path> loggerFilePath, BlackjackRuleset ruleset)
	{
		assert listener != null : "listener == null";
		assert loggerFilePath != null : "loggerFilePath == null";
		assert ruleset != null : "ruleset == null";
		this.ruleset = ruleset;
		dealer = new Dealer(
			getRuleset().getIncludedRanks(),
			getRuleset().getConfig().getShoeDeckCount(),
			getRuleset().getConfig().getShoePenetration()
		);
		this.listener = listener;
		if (getRuleset().getConfig().isLoggingEnabled())
		{
			try
			{
				logger = LoggerUtils.getFileLogger(loggerFilePath, true);
			}
			catch (IOException e)
			{
				throwException(new UncheckedIOException(e), BlackjackEngine.class.getSimpleName());
			}
		}
		else
		{
			logger = Logger.getAnonymousLogger();
			logger.setLevel(Level.OFF);
		}
		player = new Player();
		getPlayer().setChips(ruleset.getConfig().getPlayerInitialChips());
		state = BlackjackEngineState.START;
	}

	public BigDecimal acceptInsuranceBet()
	{
		final var methodName = "acceptInsuranceBet";
		getLogger().entering(BlackjackEngine.class.getSimpleName(), methodName);
		assert getActiveHandContextIndex() == HandContextType.MAIN.ordinal() : "activeHandContextIndex != HandContextType.MAIN.ordinal()";
		assert getState() == BlackjackEngineState.INSURANCE_CHECK : "getState() != BlackjackEngineState.INSURANCE_CHECK";
		if (!ruleset.isInsuranceBetPossible(getActiveHandContext(), getState(), getPlayer(), getDealer().getHand()))
		{
			if (getActiveHandContext().isAltered())
			{
			  	throwException(new IllegalHandOperationException(
					getActiveHandContext(),
					String.format(
						"Player cannot place an insurance side bet with a hand with more than %d cards.",
						BlackjackConstants.INITIAL_CARD_COUNT
					)
				), methodName);
			}
			else if (getDealer().getHand().getCards().getFirst().getRank() != Rank.ACE)
			{
				throwException(new IllegalHandOperationException(getActiveHandContext(), "Player cannot place an insurance side bet when the dealer's up card isn't an ace."), methodName);
			}
			else if (getPlayer().getContexts().size() != BlackjackConstants.INITIAL_HAND_COUNT)
			{
				throwException(new IllegalHandOperationException(getActiveHandContext(), "Player cannot place an insurance side bet when they have already split."), methodName);
			}
			else if (getPlayer().getChips().compareTo(getActiveHandContext().getBet().getHalf()) < 0)
			{
				throwException(new InsufficientChipsException(getPlayer(), getActiveHandContext().getBet().getHalf()), methodName);
			}
			else
			{
				throwException(new RuleViolationException(BlackjackConstants.RULE_VIOLATION_MESSAGE), methodName);
			}
		}
		final var amount = getActiveHandContext().getBet().getHalf();
		var playerWinnings = BigDecimal.ZERO;
		getPlayer().setChips(getPlayer().getChips().subtract(amount));
		final var insurancePot = new Pot(amount);
		final var insuranceRatio = getRuleset().getPayoutRatios().get(BlackjackConstants.INSURANCE_RATIO_KEY);
		if (getRuleset().isHandBlackjack(getDealer().getHand()))
		{
			playerWinnings = insurancePot.scoop().multiply(insuranceRatio.getPayoutMultiplier());
			getPlayer().setChips(getPlayer().getChips().add(playerWinnings));
		}
		getLogger().exiting(BlackjackEngine.class.getSimpleName(), methodName);
		return playerWinnings;
	}

	public void advanceAfterDeal()
	{
		final var methodName = "advanceAfterDeal";
		getLogger().entering(BlackjackEngine.class.getSimpleName(), methodName);
		assert getActiveHandContextIndex() == HandContextType.MAIN.ordinal() : "activeHandContextIndex != HandContextType.MAIN.ordinal()";
		assert getState() == BlackjackEngineState.DEALING : "getState() != BlackjackEngineState.DEALING";
		if (getRuleset().isInsuranceBetPossible(getActiveHandContext(), getState(), getPlayer(), getDealer().getHand()))
		{
			getListener().onInsuranceBetOpportunityDetected(getDealer().getHand().getCards().getFirst());
			setState(BlackjackEngineState.INSURANCE_CHECK);
		}
		else if (getRuleset().isHandBlackjack(getActiveHandContext().getHand()) || (getRuleset().isHandBlackjack(getDealer().getHand()) && getRuleset().shouldDealerPeekForBlackjack()))
		{
			setState(BlackjackEngineState.SHOWING_DOWN);
		}
		else
		{
			onDrawingRoundStartedPlayer();
		}
		getLogger().exiting(BlackjackEngine.class.getSimpleName(), methodName);
	}

	public void advanceAfterDealerTurn()
	{
		final var methodName = "advanceAfterDealerTurn";
		getLogger().entering(BlackjackEngine.class.getSimpleName(), methodName);
		assert getActiveHandContextIndex() == getPlayer().getContexts().size() - 1 : "getActiveHandContextIndex() != getPlayer().getContexts().size() - 1";
		assert getState() == BlackjackEngineState.DEALER_TURN : "getState() != BlackjackEngineState.DEALER_TURN";
		setState(BlackjackEngineState.SHOWING_DOWN);
		getLogger().exiting(BlackjackEngine.class.getSimpleName(), methodName);
	}

	public void advanceAfterDrawingRoundCompletedPlayer()
	{
		final var methodName = "advanceAfterDrawingRoundCompletedPlayer";
		getLogger().entering(BlackjackEngine.class.getSimpleName(), methodName);
		assert getState() == BlackjackEngineState.PLAYER_TURN;
		if (getActiveHandContextIndex() < getPlayer().getContexts().size() - 1)
		{
			setActiveHandContextIndex(getActiveHandContextIndex() + 1);
			onDrawingRoundStartedPlayer();
		}
		else
		{
			advanceAfterPlayerTurn();
		}
		getLogger().exiting(BlackjackEngine.class.getSimpleName(), methodName);
	}

	public void advanceAfterInsuranceBet(BigDecimal winnings)
	{
		final var methodName = "advanceAfterInsuranceBet";
		getLogger().entering(BlackjackEngine.class.getSimpleName(), methodName);
		assert getActiveHandContextIndex() == HandContextType.MAIN.ordinal() : "activeHandContextIndex != HandContextType.MAIN.ordinal()";
		assert getState() == BlackjackEngineState.INSURANCE_CHECK : "getState() != BlackjackEngineState.INSURANCE_CHECK";
		if (getRuleset().isHandBlackjack(getDealer().getHand()))
		{

			getListener().onInsuranceBetResolved(true, winnings);
			setState(BlackjackEngineState.SHOWING_DOWN);
		}
		else
		{
			getListener().onInsuranceBetResolved(false, winnings);
			onDrawingRoundStartedPlayer();
		}
		getLogger().exiting(BlackjackEngine.class.getSimpleName(), methodName);
	}

	public void advanceAfterPlayerTurn()
	{
		final var methodName = "advanceAfterPlayerTurn";
		getLogger().entering(BlackjackEngine.class.getSimpleName(), methodName);
		assert getActiveHandContextIndex() == getPlayer().getContexts().size() - 1 : "getActiveHandContextIndex() != getPlayer().getContexts().size() - 1";
		assert getState() == BlackjackEngineState.PLAYER_TURN : "getState() != BlackjackEngineState.PLAYER_TURN";
		final var playerHasLiveHandContext = getPlayer().getContexts().stream()
			.anyMatch(handContext -> !handContext.isSurrendered() && !getRuleset().isHandBusted(handContext.getHand()));
		if (playerHasLiveHandContext)
		{
			setState(BlackjackEngineState.DEALER_TURN);
		}
		else
		{
			setState(BlackjackEngineState.SHOWING_DOWN);
		}
		getLogger().exiting(BlackjackEngine.class.getSimpleName(), methodName);
	}

	public void advanceAfterReset()
	{
		final var methodName = "advanceAfterReset";
		getLogger().entering(BlackjackEngine.class.getSimpleName(), methodName);
		assert getState() == BlackjackEngineState.RESETTING : "getState() != BlackjackEngineState.RESETTING";
		if (getPlayer().getChips().compareTo(BigDecimal.ZERO) > 0)
		{
			setState(BlackjackEngineState.BETTING);
		}
		else
		{
			getLogger().info("The player has busted.");
			getListener().onGameCompleted();
			setState(BlackjackEngineState.END);
		}
		getLogger().exiting(BlackjackEngine.class.getSimpleName(), methodName);
	}

	public void advanceAfterShowdown()
	{
		final var methodName = "advanceAfterShowdown";
		getLogger().entering(BlackjackEngine.class.getSimpleName(), methodName);
		assert getActiveHandContextIndex() == getPlayer().getContexts().size() - 1 : "getActiveHandContextIndex() != getPlayer().getContexts().size() - 1";
		assert getState() == BlackjackEngineState.SHOWING_DOWN_FINAL_HAND : "getState() != BlackjackEngineState.SHOWING_DOWN_FINAL_HAND";
		setState(BlackjackEngineState.RESETTING);
		getLogger().exiting(BlackjackEngine.class.getSimpleName(), methodName);
	}

	private Card changeDealerCardSourceAndDraw(NoMoreCardsException e)
	{
		final var methodName = "changeDealerCardSource";
		getLogger().entering(BlackjackEngine.class.getSimpleName(), methodName);
		assert e != null : "e == null";
		getDealer().setCardSource(new Deck());
		try
		{
			getLogger().exiting(BlackjackEngine.class.getSimpleName(), methodName);
			return getDealer().getCardSource().draw();
		}
		catch (NoMoreCardsException ex)
		{
			throwException(new UncheckedIOException(ex), methodName);
			return null;
		}
	}

	public void deal()
	{
		final var methodName = "deal";
		getLogger().entering(BlackjackEngine.class.getSimpleName(), methodName);
		assert getActiveHandContextIndex() == HandContextType.MAIN.ordinal() : "activeHandContextIndex != HandContextType.MAIN.ordinal()";
		assert getState() == BlackjackEngineState.DEALING : "getState() != BlackjackEngineState.DEALING";
		final var dealerHand = getDealer().getHand();
		assert dealerHand.getCards().isEmpty() : "!dealerHand.getCards().isEmpty()";
		assert getActiveHandContext().getHand().getCards().isEmpty() : "!getActiveHandContext().getHand().getCards().isEmpty()";
		for (int count = 0; count < BlackjackConstants.INITIAL_CARD_COUNT; count++)
		{
			dealCardForPlayer();
			dealCardForDealer();
		}
		assert getActiveHandContext().getHand().getCards().size() == BlackjackConstants.INITIAL_CARD_COUNT : "getActiveHandContext().getHand().getCards().size() != RuleConfig.INITIAL_CARD_COUNT";
		assert dealerHand.getCards().size() == BlackjackConstants.INITIAL_CARD_COUNT : "dealerHand.getCards().size() != RuleConfig.INITIAL_CARD_COUNT";
		getLogger().info(String.format(
			"The cards have been dealt. Player's hand: %s. The dealer's up card is %s.",
			getActiveHandContext(),
			dealerHand.getCards().get(BlackjackConstants.INITIAL_CARD_COUNT - 1)
		));
		getLogger().exiting(BlackjackEngine.class.getSimpleName(), methodName);
	}

	private void dealCardForDealer()
	{
		final var methodName = "dealCardForDealer";
		getLogger().entering(BlackjackEngine.class.getSimpleName(), methodName);
		assert getState() == BlackjackEngineState.DEALING || getState() == BlackjackEngineState.DEALER_TURN : "getState() != BlackjackEngineState.DEALING && getState() != BlackjackEngineState.DEALER_TURN";
		Card card;
		try
		{
			card = getDealer().getCardSource().draw();
		}
		catch (NoMoreCardsException e)
		{
			card = changeDealerCardSourceAndDraw(e);
		}
		getDealer().getHand().addCards(card);
		onCardDealtToDealer(card);
		getLogger().exiting(BlackjackEngine.class.getSimpleName(), methodName, card);
	}

	private void dealCardForPlayer()
	{
		final var methodName = "dealCardForPlayer";
		getLogger().entering(BlackjackEngine.class.getSimpleName(), methodName);
		assert getState() == BlackjackEngineState.DEALING || getState() == BlackjackEngineState.PLAYER_TURN : "getState() != BlackjackEngineState.DEALING && getState() != BlackjackEngineState.PLAYER_TURN";
		Card card;
		try
		{
			card = getDealer().getCardSource().draw();
		}
		catch (NoMoreCardsException e)
		{
			card = changeDealerCardSourceAndDraw(e);
		}
		getActiveHandContext().getHand().addCards(card);
		onCardDealtToPlayer(card);
		getLogger().exiting(BlackjackEngine.class.getSimpleName(), methodName, card);
	}

	public void dealerTurn()
	{
		final var methodName = "dealerTurn";
		getLogger().entering(BlackjackEngine.class.getSimpleName(), methodName);
		assert getActiveHandContextIndex() == getPlayer().getContexts().size() - 1 : "getActiveHandContextIndex() != getPlayer().getContexts().size() - 1";
		assert getState() == BlackjackEngineState.DEALER_TURN : "getState() != BlackjackEngineState.DEALER_TURN";
		onDrawingRoundStartedDealer();
		while (getRuleset().isDealerTurnActive(getState(), getDealer()))
		{
			dealCardForDealer();
		}
		onDrawingRoundCompletedDealer();
		getLogger().exiting(BlackjackEngine.class.getSimpleName(), methodName);
	}

	public void declineInsuranceBet()
	{
		final var methodName = "declineInsuranceBet";
		getLogger().entering(BlackjackEngine.class.getSimpleName(), methodName);
		assert getActiveHandContextIndex() == HandContextType.MAIN.ordinal() : "activeHandContextIndex != HandContextType.MAIN.ordinal()";
		assert getState() == BlackjackEngineState.INSURANCE_CHECK : "getState() != BlackjackEngineState.INSURANCE_CHECK";
		if (getRuleset().isHandBlackjack(getDealer().getHand()) || getRuleset().isHandBlackjack(getActiveHandContext().getHand()))
		{
			setState(BlackjackEngineState.SHOWING_DOWN);
		}
		else
		{
			onDrawingRoundStartedPlayer();
		}
		getLogger().exiting(BlackjackEngine.class.getSimpleName(), methodName);
	}

	private void drawCardForPlayerAction()
	{
		final var methodName = "drawCardForPlayerAction";
		getLogger().entering(BlackjackEngine.class.getSimpleName(), methodName);
		assert getState() == BlackjackEngineState.PLAYER_TURN : "getState() != BlackjackEngineState.PLAYER_TURN";
		dealCardForPlayer();
		if (getRuleset().isHandBusted(getActiveHandContext().getHand()))
		{
			onDrawingRoundCompletedPlayer();
		}
		getLogger().exiting(BlackjackEngine.class.getSimpleName(), methodName);
	}

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
		return dealer;
	}

	public BlackjackEngineListener getListener()
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

	public BlackjackRuleset getRuleset()
	{
		return ruleset;
	}

	public BlackjackEngineState getState()
	{
		return state;
	}

	private void onBetPlaced()
	{
		final var methodName = "onBetPlaced";
		getLogger().entering(BlackjackEngine.class.getSimpleName(), methodName);
		assert getActiveHandContextIndex() == HandContextType.MAIN.ordinal() : "activeHandContextIndex != HandContextType.MAIN.ordinal()";
		assert getState() == BlackjackEngineState.BETTING : "getState() != BlackjackEngineState.BETTING";
		getListener().onBetPlaced(getActiveHandContext());
		getLogger().info(String.format(
			"Player %s has placed a bet of $%,.2f on their %s hand.",
			getPlayer(),
			getActiveHandContext().getBet().getAmount(),
			StringUtils.normalizeLower(getActiveHandContext().getType().toString())
		));
		getLogger().exiting(BlackjackEngine.class.getSimpleName(), methodName);
	}

	private void onCardDealtToDealer(Card card)
	{
		final var methodName = "onCardDealtToDealer";
		getLogger().entering(BlackjackEngine.class.getSimpleName(), methodName, card);
		assert card != null : "card == null";
		assert getState() == BlackjackEngineState.DEALING || getState() == BlackjackEngineState.DEALER_TURN : "getState() != BlackjackEngineState.DEALING && getState() != BlackjackEngineState.DEALER_TURN";
		final var isFaceUpCard = getDealer().getHand().getCards().size() != BlackjackConstants.INITIAL_CARD_COUNT
			|| !getRuleset().isDealerSecondCardFaceDown();
			getListener().onCardDealtToDealer(card, getDealer().getHand(), isFaceUpCard);
			getLogger().info(String.format("Added card %s to dealer's hand %s.", card, getDealer().getHand()));
		getLogger().exiting(BlackjackEngine.class.getSimpleName(), methodName);
	}

	private void onCardDealtToPlayer(Card card)
	{
		final var methodName = "onCardDealtToPlayer";
		getLogger().entering(BlackjackEngine.class.getSimpleName(), methodName, card);
		assert card != null : "card == null";
		assert getState() == BlackjackEngineState.DEALING || getState() == BlackjackEngineState.PLAYER_TURN : "getState() != BlackjackEngineState.DEALING && getState() != BlackjackEngineState.PLAYER_TURN";
		if (getState() == BlackjackEngineState.PLAYER_TURN && getActiveHandContext().getHand().getCards().size() > BlackjackConstants.INITIAL_CARD_COUNT)
		{
			getActiveHandContext().setAltered();
		}
		getListener().onCardDealtToPlayer(card, getActiveHandContext());
		getLogger().info(String.format("Added card %s to player's hand %s.", card, getActiveHandContext()));
		getLogger().exiting(BlackjackEngine.class.getSimpleName(), methodName);
	}

	private void onDrawingRoundCompletedDealer()
	{
		final var methodName = "onDrawingRoundCompletedDealer";
		getLogger().entering(BlackjackEngine.class.getSimpleName(), methodName);
		assert getActiveHandContextIndex() == getPlayer().getContexts().size() - 1 : "getActiveHandContextIndex() != getPlayer().getContexts().size() - 1";
		assert getState() == BlackjackEngineState.DEALER_TURN : "getState() != BlackjackEngineState.DEALER_TURN";
		getListener().onDrawingRoundCompletedDealer(getDealer().getHand());
		getLogger().info("The dealer's drawing round was completed.");
		getLogger().exiting(BlackjackEngine.class.getSimpleName(), methodName);
	}

	private void onDrawingRoundCompletedPlayer()
	{
		final var methodName = "onDrawingRoundCompletedPlayer";
		getLogger().entering(BlackjackEngine.class.getSimpleName(), methodName);
		assert getState() == BlackjackEngineState.PLAYER_TURN : "getState() != BlackjackEngineState.PLAYER_TURN";
		getListener().onDrawingRoundCompletedPlayer(getActiveHandContext());
		getLogger().info("The player's drawing round was completed.");
		getLogger().exiting(BlackjackEngine.class.getSimpleName(), methodName);
	}

	private void onDrawingRoundStartedDealer()
	{
		final var methodName = "onDrawingRoundStartedDealer";
		getLogger().entering(BlackjackEngine.class.getSimpleName(), methodName);
		assert getActiveHandContextIndex() == getPlayer().getContexts().size() - 1 : "getActiveHandContextIndex() != getPlayer().getContexts().size() - 1";
		assert getState() == BlackjackEngineState.DEALER_TURN : "getState() != BlackjackEngineState.DEALER_TURN";
		getListener().onDrawingRoundStartedDealer(getDealer().getHand());
		getLogger().info("The dealer's drawing round was started.");
		getLogger().exiting(BlackjackEngine.class.getSimpleName(), methodName);
	}

	private void onDrawingRoundStartedPlayer()
	{
		final var methodName = "onDrawingRoundStartedPlayer";
		getLogger().entering(BlackjackEngine.class.getSimpleName(), methodName);
		getListener().onDrawingRoundStartedPlayer(getActiveHandContext());
		getLogger().info(String.format("The player's drawing round was started on hand %s.", getActiveHandContext()));
		setState(BlackjackEngineState.PLAYER_TURN);
		getLogger().exiting(BlackjackEngine.class.getSimpleName(), methodName);
	}

	public void placeBet(BigDecimal amount)
	{
		final var methodName = "placeBet";
		getLogger().entering(BlackjackEngine.class.getSimpleName(), methodName, amount);
		assert amount != null && amount.compareTo(BigDecimal.ZERO) > 0 : "amount == null || amount.compareTo(BigDecimal.ZERO) <= 0";
		assert getActiveHandContextIndex() == HandContextType.MAIN.ordinal() : "activeHandContextIndex != HandContextType.MAIN.ordinal()";
		assert getState() == BlackjackEngineState.BETTING : "getState() != BlackjackEngineState.BETTING";
		if (getPlayer().getChips().compareTo(amount) < 0)
		{
			throwException(new InsufficientChipsException(getPlayer(), amount), methodName);
		}
		else if (getPlayer().getChips().subtract(amount).compareTo(getRuleset().getConfig().getMinimumBetAmount()) < 0
				&& getPlayer().getChips().subtract(amount).compareTo(BigDecimal.ZERO) != 0)
		{
			throwException(
				new RuleViolationException(String.format(
					"Cannot place a bet that leaves the player with a chip amount lower than %,.2f unless it is an all-in bet.",
					getRuleset().getConfig().getMinimumBetAmount()
				)), methodName
			);
		}
		else if (getRuleset().getConfig().getMinimumBetAmount().compareTo(amount) > 0)
		{
			throwException(
				new RuleViolationException(String.format("Cannot place a bet lower than $%,.2f.", getRuleset().getConfig().getMinimumBetAmount())),
				methodName
			);
		}
		getListener().onBettingRoundStarted();
		final var playerMainHand = new HandContext(new Bet(amount), HandContextType.MAIN);
		getPlayer().addContext(playerMainHand);
		getPlayer().setChips(getPlayer().getChips().subtract(amount));
		getActiveHandContext().getPot().addChips(amount.multiply(BigDecimal.TWO));
		onBetPlaced();
		setState(BlackjackEngineState.DEALING);
		getLogger().exiting(BlackjackEngine.class.getSimpleName(), methodName, playerMainHand);
	}

	public void playerDoubleDown()
	{
		final var methodName = "playerDoubleDown";
		getLogger().entering(BlackjackEngine.class.getSimpleName(), methodName);
		assert getState() == BlackjackEngineState.PLAYER_TURN : "getState() != BlackjackEngineState.PLAYER_TURN";
		final var context = getActiveHandContext();
		if (!getRuleset().isDoublingDownPossible(getActiveHandContext(), getState(), getPlayer()))
		{
			if (getActiveHandContext().isAltered())
			{
				throwException(new IllegalHandOperationException(context, "Player cannot double down on an already altered hand"), methodName);
			}
			else if (getPlayer().getChips().compareTo(getActiveHandContext().getBet().getAmount()) < 0)
			{
				throwException(new InsufficientChipsException(getPlayer(), getActiveHandContext().getBet().getAmount()), methodName);
			}
			else
			{
				throwException(new RuleViolationException(BlackjackConstants.RULE_VIOLATION_MESSAGE), methodName);
			}
		}
		final var doubleDownAmount = getActiveHandContext().getBet().getAmount();
		getPlayer().setChips(getPlayer().getChips().subtract(doubleDownAmount));
		context.setBet(new Bet(doubleDownAmount.multiply(BigDecimal.TWO)));
		context.getPot().addChips(doubleDownAmount.multiply(BigDecimal.TWO));
		drawCardForPlayerAction();
		getLogger().info(String.format("Player has doubled down on hand %s.", context.getHand()));
		if (getState() == BlackjackEngineState.PLAYER_TURN && !getRuleset().isHandBusted(context.getHand()))
		{
			onDrawingRoundCompletedPlayer();
		}
		getLogger().exiting(BlackjackEngine.class.getSimpleName(), methodName);
	}

	public void playerHit()
	{
		final var methodName = "playerHit";
		getLogger().entering(BlackjackEngine.class.getSimpleName(), methodName);
		assert getState() == BlackjackEngineState.PLAYER_TURN : "getState() != BlackjackEngineState.PLAYER_TURN";
		drawCardForPlayerAction();
		if (getState() == BlackjackEngineState.PLAYER_TURN && !getRuleset().isHandBusted(getActiveHandContext().getHand()))
		{
			setState(BlackjackEngineState.PLAYER_TURN);
		}
		getLogger().exiting(BlackjackEngine.class.getSimpleName(), methodName);
	}
	
	public void playerSplit()
	{
		final var methodName = "playerSplit";
		getLogger().entering(BlackjackEngine.class.getSimpleName(), methodName);
		assert getState() == BlackjackEngineState.PLAYER_TURN : "getState() != BlackjackEngineState.PLAYER_TURN";
		if (!getRuleset().isSplittingPossible(getActiveHandContext(), getState(), getActiveHandContextIndex(), getPlayer()))
		{
			if (getActiveHandContext().isAltered() || !getActiveHandContext().getHand().isPocketPair())
			{
				throwException(new IllegalHandOperationException(getActiveHandContext(), "An attempt to split a non-pocket pair occurred."), methodName);
			}
			else if (getActiveHandContextIndex() >= getRuleset().getConfig().getMaximumSplitCount() + 1)
			{
				throwException(new IllegalHandOperationException(
					getActiveHandContext(),
					String.format(
						"Player cannot have more than %d hands.",
						getRuleset().getConfig().getMaximumSplitCount() + 1
					)
				), methodName);
			}
			else if (getPlayer().getChips().compareTo(getActiveHandContext().getBet().getAmount()) < 0)
			{
				throwException(new InsufficientChipsException(getPlayer(), getActiveHandContext().getBet().getAmount()), methodName);
			}
			else
			{
				throwException(new RuleViolationException(BlackjackConstants.RULE_VIOLATION_MESSAGE), methodName);
			}
		}
		final var splitAmount = getActiveHandContext().getBet().getAmount();
		getPlayer().setChips(getPlayer().getChips().subtract(splitAmount));
		final var playerSplitHandContext = new HandContext(new Bet(splitAmount), HandContextType.SPLIT);
		playerSplitHandContext.getHand().addCards(getActiveHandContext().getHand().getCards().getLast());
		getPlayer().addContext(playerSplitHandContext);
		getActiveHandContext().setSplit();
		getActiveHandContext().getHand().removeCard(BlackjackConstants.INITIAL_CARD_COUNT - 1);
		try
		{
			getActiveHandContext().getHand().addCards(getDealer().getCardSource().draw());
		}
		catch (NoMoreCardsException e)
		{
			getActiveHandContext().getHand().addCards(changeDealerCardSourceAndDraw(e));
		}
		try
		{
			playerSplitHandContext.getHand().addCards(getDealer().getCardSource().draw());
		}
		catch (NoMoreCardsException e)
		{
			playerSplitHandContext.getHand().addCards(changeDealerCardSourceAndDraw(e));
		}
		playerSplitHandContext.getPot().addChips(splitAmount.multiply(BigDecimal.TWO));
		getLogger().info(String.format(
			"Player has elected to split. Player now has a current hand of %s and a split hand of %s.",
			getPlayer().getContexts().getFirst(),
			playerSplitHandContext
		));
		getListener().onPlayerSplit(getActiveHandContext(), playerSplitHandContext);
		onDrawingRoundStartedPlayer();
		getLogger().exiting(BlackjackEngine.class.getSimpleName(), methodName);
	}

	public void playerStand()
	{
		final var methodName = "playerStand";
		getLogger().entering(BlackjackEngine.class.getSimpleName(), methodName);
		assert getState() == BlackjackEngineState.PLAYER_TURN : "getState() != BlackjackEngineState.PLAYER_TURN";
		getActiveHandContext().setAltered();
		onDrawingRoundCompletedPlayer();
		getLogger().exiting(BlackjackEngine.class.getSimpleName(), methodName);
	}

	public void playerSurrender()
	{
		final var methodName = "playerSurrender";
		getLogger().entering(BlackjackEngine.class.getSimpleName(), methodName);
		assert getState() == BlackjackEngineState.PLAYER_TURN : "getState() != BlackjackEngineState.PLAYER_TURN";
		if (!getRuleset().isSurrenderingPossible(getActiveHandContext(), getState(), getPlayer()))
		{
			if (getActiveHandContext().isAltered())
			{
				throwException(new IllegalHandOperationException(
					getActiveHandContext(),
					String.format(
						"Player cannot surrender on a hand with more than %d cards",
						BlackjackConstants.INITIAL_CARD_COUNT
					)
				), methodName);
			}
			else
			{
				throwException(new RuleViolationException(BlackjackConstants.RULE_VIOLATION_MESSAGE), methodName);
			}
		}
		getActiveHandContext().setSurrendered();
		onDrawingRoundCompletedPlayer();
		getLogger().exiting(BlackjackEngine.class.getSimpleName(), methodName);
	}

	public void reset()
	{
		final var methodName = "reset";
		getLogger().entering(BlackjackEngine.class.getSimpleName(), methodName);
		assert getActiveHandContextIndex() == getPlayer().getContexts().size() - 1 : "getActiveHandContextIndex() != getPlayer().getContexts().size() - 1";
		assert getState() == BlackjackEngineState.RESETTING : "getState() != BlackjackEngineState.RESETTING";
		if ((getDealer().getCardSource() instanceof Shoe shoe
				&& shoe.getCards().size() <= shoe.getCutoffAmount())
			|| getDealer().getCardSource() instanceof Deck)
		{
			getDealer().setCardSource(
				new Shoe(
					getRuleset().getIncludedRanks(),
					BlackjackConstants.DEFAULT_SHOE_DECK_COUNT,
					getRuleset().getConfig().getShoePenetration()
				)
			);
		}
		getDealer().setHand(new Hand());
		getPlayer().clearContexts();
		setActiveHandContextIndex(0);
		showdownHandContextIterator = null;
		getListener().onReset();
		getLogger().exiting(BlackjackEngine.class.getSimpleName(), methodName);
	}

	private void setActiveHandContextIndex(int contextIndex)
	{
		final var methodName = "setActiveHandContextIndex";
		getLogger().entering(BlackjackEngine.class.getSimpleName(), methodName, contextIndex);
		assert getState() == BlackjackEngineState.PLAYER_TURN || getState() == BlackjackEngineState.RESETTING : "getState() != BlackjackEngineState.PLAYER_TURN && getState() != BlackjackEngineState.RESETTING";
		assert contextIndex >= 0 && contextIndex <= getPlayer().getContexts().size() : "handIndex < 0 && handIndex > getPlayer().getHands().size()";
		activeHandContextIndex = contextIndex;
		getLogger().exiting(BlackjackEngine.class.getSimpleName(), methodName);
	}

	private void setState(BlackjackEngineState state)
	{
		final var methodName = "setState";
		getLogger().entering(BlackjackEngine.class.getSimpleName(), methodName, state);
		assert state != null : "state == null";
		final var oldState = getState();
		this.state = state;
		getListener().onStateChanged(oldState);
		getLogger().exiting(BlackjackEngine.class.getSimpleName(), methodName);
	}

	public void showdown()
	{
		final var methodName = "showdown";
		getLogger().entering(BlackjackEngine.class.getSimpleName(), methodName);
		assert getActiveHandContextIndex() == getPlayer().getContexts().size() - 1 : "getActiveHandContextIndex() != getPlayer().getContexts().size() - 1";
		assert getState() == BlackjackEngineState.SHOWING_DOWN : "getState() != BlackjackEngineState.SHOWING_DOWN";
		assert showdownHandContextIterator == null || showdownHandContextIterator.hasNext() : "getShowdownHandContextIterator() != null && !getShowdownHandContextIterator().hasNext()";
		if (showdownHandContextIterator == null)
		{
			showdownHandContextIterator = Arrays.stream(getRuleset().getHandContextsInShowdownOrder(getPlayer())).iterator();
		}
		final var currentHandContext = showdownHandContextIterator.next();
		final var payoutRatios = getRuleset().getPayoutRatios();
		var playerBeatDealer = false;
		var playerWinnings = BigDecimal.ZERO;
		if (!showdownHandContextIterator.hasNext())
		{
			setState(BlackjackEngineState.SHOWING_DOWN_FINAL_HAND);
		}
		getListener().onShowdownStarted(getDealer().getHand(), currentHandContext);
		if (currentHandContext.isSurrendered())
		{
			playerWinnings = currentHandContext.getPot().scoop().multiply(
				payoutRatios.get(BlackjackConstants.SURRENDER_RATIO_KEY).getPayoutMultiplier()
			).setScale(BlackjackConstants.DEFAULT_CHIP_SCALE, RoundingMode.HALF_DOWN);
		}
		else if (getRuleset().isHandBusted(currentHandContext.getHand()))
		{
			currentHandContext.getPot().scoop();
		}
		else if (getRuleset().isHandBusted(getDealer().getHand()))
		{
			playerBeatDealer = true;
			playerWinnings = currentHandContext.getPot().scoop().setScale(BlackjackConstants.DEFAULT_CHIP_SCALE, RoundingMode.HALF_DOWN);
		}
		else
		{
			if (currentHandContext.getHand().getScore() == getDealer().getHand().getScore())
			{
				playerWinnings = currentHandContext.getPot().scoop().multiply(
					payoutRatios.get(BlackjackConstants.PUSH_RATIO_KEY).getPayoutMultiplier()
				).setScale(BlackjackConstants.DEFAULT_CHIP_SCALE, RoundingMode.HALF_DOWN);
			}
			else if (currentHandContext.getHand().getScore() > getDealer().getHand().getScore())
			{
				playerBeatDealer = true;

				if (getRuleset().isHandBlackjack(currentHandContext.getHand())
					&& currentHandContext.getType() == HandContextType.MAIN
					&& !showdownHandContextIterator.hasNext())
				{
					playerWinnings = currentHandContext.getPot().scoop().multiply(
						payoutRatios.get(BlackjackConstants.BLACKJACK_RATIO_KEY).getPayoutMultiplier()
					).setScale(BlackjackConstants.DEFAULT_CHIP_SCALE, RoundingMode.HALF_DOWN);
				}
				else
				{
					playerWinnings = currentHandContext.getPot().scoop().setScale(BlackjackConstants.DEFAULT_CHIP_SCALE, RoundingMode.HALF_DOWN);
				}
			}
		}
		getPlayer().setChips(getPlayer().getChips().add(playerWinnings));
		getLogger().info(String.format("Player's hand %s was showed down against dealer's hand %s.", currentHandContext, getDealer().getHand()));
		getListener().onShowdownCompleted(getDealer().getHand(), currentHandContext, playerBeatDealer, playerWinnings);
		if (showdownHandContextIterator.hasNext())
		{
			setState(BlackjackEngineState.SHOWING_DOWN);
		}
		else
		{
			getListener().onBettingRoundCompleted();
		}
		getLogger().exiting(BlackjackEngine.class.getSimpleName(), methodName);
	}

	public void start()
	{
		final var methodName = "start";
		getLogger().entering(BlackjackEngine.class.getSimpleName(), methodName);
		assert getState() == null || getState() == BlackjackEngineState.START : "getState() != null && getState() != BlackjackEngineState.START";
		getListener().onGameStarted();
		setState(BlackjackEngineState.BETTING);
		getLogger().exiting(BlackjackEngine.class.getSimpleName(), methodName);
	}

	private void throwException(RuntimeException e, String sourceMethod)
	{
		assert e != null : "e == null";
		assert sourceMethod != null && !sourceMethod.isBlank() : "sourceMethod == null || sourceMethod.isBlank() ";
		getLogger().throwing(BlackjackEngine.class.getSimpleName(), sourceMethod, e);
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
			getRuleset(),
			getState()
		);
	}
}