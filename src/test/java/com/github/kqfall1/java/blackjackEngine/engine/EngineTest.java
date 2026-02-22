package com.github.kqfall1.java.blackjackEngine.engine;

import com.github.kqfall1.java.blackjackEngine.model.engine.BlackjackEngine;
import com.github.kqfall1.java.blackjackEngine.model.cards.Card;
import com.github.kqfall1.java.blackjackEngine.model.engine.*;
import com.github.kqfall1.java.blackjackEngine.model.enums.EngineState;
import com.github.kqfall1.java.blackjackEngine.model.enums.HandContextType;
import com.github.kqfall1.java.blackjackEngine.model.enums.Rank;
import com.github.kqfall1.java.blackjackEngine.model.hands.Hand;
import com.github.kqfall1.java.blackjackEngine.model.hands.HandContext;
import com.github.kqfall1.java.handlers.input.ConsoleHandler;
import com.github.kqfall1.java.blackjackEngine.model.interfaces.BlackjackEngineListener;
import com.github.kqfall1.java.blackjackEngine.model.interfaces.BlackjackRuleset;
import java.math.BigDecimal;
import java.util.logging.Level;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

/**
 * Provides abstraction for creating {@code BlackjackEngine}-related tests.
 *
 * @author kqfall1
 * @since 22/12/2025
 */
public abstract class EngineTest
{
	public BlackjackEngine engine;
	public ConsoleHandler handler;
	public static final BigDecimal INITIAL_PLAYER_CHIP_AMOUNT = BigDecimal.valueOf(5000);
	public String logFilePath;
	public String loggerName;
	public BlackjackRuleset ruleset;
	public static final int TEST_ITERATIONS = 200;

	public final BigDecimal advanceToDealerTurn(BigDecimal maximumBetAmount)
	{
		final var BET_AMOUNT = advanceToPlayerTurn(maximumBetAmount);

		if (engine.getState() == EngineState.PLAYER_TURN)
		{
			engine.playerStand();
			engine.dealerTurn();
		}

		return BET_AMOUNT;
	}

	public final void advanceToEndOfRound()
	{
		if (engine.getState() == EngineState.DEALER_TURN)
		{
			engine.dealerTurn();
			engine.advanceAfterDealerTurn();
		}

		if (engine.getState() == EngineState.SHOWDOWN)
		{
			engine.showdown();
			engine.advanceAfterShowdown();
		}

		engine.reset();
		engine.advanceAfterReset();
	}

	public final BigDecimal advanceToPlayerTurn(BigDecimal maximumBetAmount)
	{
		final var BET_AMOUNT = placeRandomHandBet(maximumBetAmount);
		engine.deal();
		engine.advanceAfterDeal();
		declinePossibleInsuranceBet();
		return BET_AMOUNT;
	}

	public final void declinePossibleInsuranceBet()
	{
		if (engine.getState() == EngineState.INSURANCE_CHECK)
		{
			engine.declineInsuranceBet();
		}
	}

	@BeforeEach
	public abstract void init();

	public final void initDependencies()
	{
		handler = new ConsoleHandler();
		final var CONFIG = new BlackjackRulesetConfiguration();
		CONFIG.setPlayerInitialChips(INITIAL_PLAYER_CHIP_AMOUNT);
		ruleset = new StandardBlackjackRuleset(CONFIG);
	}

	public final void initEngine(String logFilePath, String loggerName)
	{
		this.logFilePath = logFilePath;
		this.loggerName = loggerName;
		start();
		engine.getLogger().setLevel(Level.OFF);
	}

	@RepeatedTest(TEST_ITERATIONS)
	public abstract void main();

	final BlackjackEngineListener LISTENER = new BlackjackEngineListener()
	{
		@Override
		public void onBetPlaced(HandContext handContext)
		{
			assertEquals(
				HandContextType.MAIN.ordinal(),
				engine.getActiveHandContextIndex()
			);
			assertTrue(engine.getState() == EngineState.BETTING);
			assertTrue(nearlyEquals(
				handContext.getBet().getAmount()
					.multiply(BigDecimal.TWO),
				handContext.getPot().getAmount(),
				BlackjackConstants.DEFAULT_CHIP_SCALE
			));
			handler.getOut().printf(
				"You placed a bet of $%.2f.\n",
				handContext.getBet().getAmount()
			);
		}

		@Override
		public void onBettingRoundCompleted()
		{
			assertEquals(
				engine.getPlayer().getContexts().size() - 1,
				engine.getActiveHandContextIndex()
			);
			assertEquals(EngineState.SHOWDOWN, engine.getState());
			handler.getOut().println("You have completed a betting round.");
		}

		@Override
		public void onBettingRoundStarted()
		{
			assertEquals(
				HandContextType.MAIN.ordinal(),
				engine.getActiveHandContextIndex()
			);
			assertEquals(EngineState.BETTING, engine.getState());
			handler.getOut().println("You have started a new betting round! Good luck!");
		}

		@Override
		public void onCardDealtToDealer(Card card, Hand dealerHand, boolean isFaceUp)
		{
			assertEquals(
				engine.getPlayer().getContexts().size() - 1,
				engine.getActiveHandContextIndex()
			);
			assertTrue(
				engine.getState() == EngineState.DEALING
				|| engine.getState() == EngineState.DEALER_TURN
			);

			if (isFaceUp)
			{
				handler.getOut().printf(
					"The dealer was dealt the %s.\n",
					card.toStringPretty()
				);
			}
		}

		@Override
		public void onCardDealtToPlayer(Card card, HandContext handContext)
		{
			assertTrue(
				engine.getState() == EngineState.DEALING
				|| engine.getState() == EngineState.PLAYER_TURN
			);

			if (handContext.getHand().getCards().size() > BlackjackConstants.INITIAL_CARD_COUNT)
			{
				assertTrue(handContext.isAltered());
			}

			handler.getOut().printf(
				"You were dealt the %s. Your current hand is now %s.\n",
				card.toStringPretty(),
				handContext.getHand().toStringPretty()
			);
		}

		@Override
		public void onDrawingRoundCompletedDealer(Hand dealerHand)
		{
			assertEquals(
				engine.getPlayer().getContexts().size() - 1,
				engine.getActiveHandContextIndex()
			);
			assertEquals(EngineState.DEALER_TURN, engine.getState());
			handler.getOut().printf(
				"The dealer has finished drawing. Their hand is %s.\n",
				dealerHand.toStringPretty()
			);
		}

		@Override
		public void onDrawingRoundCompletedPlayer(HandContext handContext) {
			assertEquals(EngineState.PLAYER_TURN, engine.getState());
			handler.getOut().printf(
				"You have completed a drawing round on hand %s.\n",
				handContext.getHand().toStringPretty()
			);
		}

		@Override
		public void onDrawingRoundStartedDealer(Hand dealerHand)
		{
			assertEquals(
				engine.getPlayer().getContexts().size() - 1,
				engine.getActiveHandContextIndex()
			);
			assertEquals(EngineState.DEALER_TURN, engine.getState());
			handler.getOut().println("The dealer has begun drawing.");
		}

		@Override
		public void onDrawingRoundStartedPlayer(HandContext handContext)
		{
			assertTrue(engine.getState() == EngineState.DEALING
				|| engine.getState() == EngineState.INSURANCE_CHECK
				|| engine.getState() == EngineState.PLAYER_TURN);
			handler.getOut().println("You have begun a new drawing round.");
		}

		@Override
		public void onGameCompleted()
		{
			assertEquals(
				HandContextType.MAIN.ordinal(),
				engine.getActiveHandContextIndex()
			);
			assertEquals(EngineState.RESETTING, engine.getState());
			handler.getOut().println("Thanks for playing!");
		}

		@Override
		public void onGameStarted()
		{
			assertEquals(
				HandContextType.MAIN.ordinal(),
				engine.getActiveHandContextIndex()
			);
			assertEquals(EngineState.START, engine.getState());
			handler.getOut().println("Welcome to the table!");
		}

		@Override
		public void onInsuranceBetOpportunityDetected(Card dealerUpCard)
		{
			assertEquals(
				HandContextType.MAIN.ordinal(),
				engine.getActiveHandContextIndex()
			);
			assertEquals(EngineState.DEALING, engine.getState());
			assertEquals(Rank.ACE, dealerUpCard.getRank());
			handler.getOut().printf(
				"The dealer is showing the %s. You are eligible to place an insurance side bet.\n",
				dealerUpCard.toStringPretty()
			);
		}

		@Override
		public void onInsuranceBetResolved(boolean wasSuccessful, BigDecimal playerWinnings)
		{
			assertEquals(
				HandContextType.MAIN.ordinal(),
				engine.getActiveHandContextIndex()
			);
			assertEquals(EngineState.INSURANCE_CHECK, engine.getState());

			if (wasSuccessful)
			{
				handler.getOut().printf(
					"You have won your insurance bet and collect $%.2f.\n",
					playerWinnings
				);
			}
			else
			{
				handler.getOut().println("You have lost your insurance bet.");
			}
		}

		@Override
		public void onPlayerSplit(HandContext currentHand, HandContext splitHand)
		{
			assertEquals(EngineState.PLAYER_TURN, engine.getState());
			handler.getOut().printf(
				String.format(
					"Your current hand is now %s and your split hand is %s.\n",
					currentHand.getHand().toStringPretty(),
					splitHand.getHand().toStringPretty()
				)
			);
		}

		@Override
		public void onReset()
		{
//			assertEquals(
//				HandContextType.MAIN.ordinal(),
//				engine.getActiveHandContextIndex()
//			);
			assertEquals(EngineState.RESETTING, engine.getState());
			handler.getOut().println("The dealer is initializing a new betting round...");
		}

		@Override
		public void onShowdownCompleted(Hand dealerHand, HandContext handContext,
										boolean playerWon, BigDecimal playerWinnings)
		{
			assertEquals(
				engine.getPlayer().getContexts().size() - 1,
				engine.getActiveHandContextIndex()
			);
			assertEquals(EngineState.SHOWDOWN, engine.getState());

			final var completedString = String.format(
				"Your score is %d and the dealer's score is %d.",
				handContext.getHand().getScore(),
				dealerHand.getScore()
			);

			if (playerWon)
			{
				handler.getOut().printf(
					"%s You have won the showdown and collect $%.2f.\n",
					completedString,
					playerWinnings
				);
			}
			else if (playerWinnings.compareTo(BigDecimal.ZERO) > 0)
			{
				handler.getOut().printf(
					"%s You did not win the showdown, yet still collect $%.2f.\n",
					completedString,
					playerWinnings
				);
			}
			else
			{
				handler.getOut().printf(
					"%s You have lost the showdown.\n",
					completedString
				);
			}
		}

		@Override
		public void onShowdownStarted(Hand dealerHand, HandContext handContext)
		{
			assertEquals(
				engine.getPlayer().getContexts().size() - 1,
				engine.getActiveHandContextIndex()
			);
			assertEquals(EngineState.SHOWDOWN, engine.getState());
			handler.getOut().printf(
				"Your hand %s is being shown down against the dealer's hand %s.\n",
				handContext.getHand().toStringPretty(),
				dealerHand.toStringPretty()
			);
		}

		@Override
		public void onStateChanged(EngineState oldState) {}
	};

	public static boolean nearlyEquals
	(BigDecimal expectedValue, BigDecimal actualValue, int scale)
	{
		Assertions.assertTrue(scale > 0);
		var threshold = BigDecimal.ONE.movePointLeft(scale);

		return expectedValue
			.subtract(actualValue)
			.abs()
			.compareTo(threshold) <= 0;
	}

	public final BigDecimal placeRandomHandBet(BigDecimal maximumBetAmount)
	{
		final var BET_AMOUNT = maximumBetAmount
			.subtract(ruleset.getConfig().getMinimumBetAmount())
			.multiply(BigDecimal.valueOf(Math.random()))
			.add(ruleset.getConfig().getMinimumBetAmount());
		engine.placeHandBet(BET_AMOUNT);
		return BET_AMOUNT;
	}

	private void start()
	{
		engine = new BlackjackEngine(LISTENER, logFilePath, loggerName, ruleset);
		engine.start();
	}
}