package com.github.kqfall1.java.blackjackEngine.controllers;

import com.github.kqfall1.java.blackjackEngine.model.cards.Card;
import com.github.kqfall1.java.blackjackEngine.model.cards.Rank;
import com.github.kqfall1.java.blackjackEngine.model.engine.EngineState;
import com.github.kqfall1.java.blackjackEngine.model.engine.StandardRuleConfig;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import com.github.kqfall1.java.blackjackEngine.model.hands.Hand;
import com.github.kqfall1.java.blackjackEngine.model.hands.HandContext;
import com.github.kqfall1.java.blackjackEngine.model.interfaces.EngineListener;
import com.github.kqfall1.java.handlers.input.ConsoleHandler;
import java.io.IOException;
import java.math.BigDecimal;
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
	public StandardRuleConfig config;
	public BlackjackEngine engine;
	public ConsoleHandler handler;
	public static final BigDecimal INITIAL_PLAYER_CHIP_AMOUNT = BigDecimal.valueOf(5000);
	public String logFilePath;
	public String loggerName;
	public static final int TEST_ITERATIONS = 5000;

	public BigDecimal advanceToDealerTurn(BigDecimal maximumBetAmount)
	throws Exception
	{
		final var BET_AMOUNT = advanceToPlayerTurn(maximumBetAmount);

		if (engine.getState() == EngineState.PLAYER_TURN)
		{
			engine.playerStand();
		}

		engine.advanceAfterPlayerTurn();
		return BET_AMOUNT;
	}

	public BigDecimal advanceToPlayerTurn(BigDecimal maximumBetAmount)
	throws Exception
	{
		final var BET_AMOUNT = placeRandomHandBet(maximumBetAmount);
		engine.deal();
		engine.advanceAfterDeal();
		declinePotentialInsuranceBet();
		return BET_AMOUNT;
	}

	public final void advanceToEndOfRoundAfterPotentialDealerTurn()
	throws InsufficientChipsException
	{
		if (engine.getState() == EngineState.DEALER_TURN)
		{
			engine.advanceAfterDealerTurn();
			engine.advanceAfterShowdown();
			engine.advanceAfterReset();
		}
	}

	public final void declinePotentialInsuranceBet() throws InsufficientChipsException
	{
		if (engine.getState() == EngineState.INSURANCE_CHECK)
		{
			engine.declineInsuranceBet();
		}
	}

	@BeforeEach
	public abstract void init() throws InsufficientChipsException, IOException;

	public void initDependencies()
	{
		config = new StandardRuleConfig();
		config.setPlayerInitialChips(INITIAL_PLAYER_CHIP_AMOUNT);
		handler = new ConsoleHandler();
	}

	public void initEngine(String logFilePath, String loggerName)
	throws InsufficientChipsException, IOException
	{
		this.logFilePath = logFilePath;
		this.loggerName = loggerName;
		start();
	}

	@RepeatedTest(TEST_ITERATIONS)
	public abstract void main() throws Exception;

	final EngineListener LISTENER = new EngineListener()
	{
		@Override
		public void onBetPlaced(HandContext handContext)
		{
			var activeHandContextIndex = switch (handContext.getType())
			{
				case MAIN -> 0;
				case SPLIT -> 1;
			};
			assertEquals(activeHandContextIndex, engine.getActiveHandContextIndex());
			assertTrue(
				engine.getState() == EngineState.BETTING
				|| engine.getState() == EngineState.INSURANCE_CHECK
				|| engine.getState() == EngineState.PLAYER_TURN);
			assertEquals(
				0,
				handContext.getPot().getAmount().compareTo(handContext.getBet().getAmount().multiply(BigDecimal.TWO))
			);
			handler.getOut().printf(
				"You placed a bet of $%.2f.\n",
				handContext.getBet().getAmount()
			);
		}

		@Override
		public void onBettingRoundCompleted()
		{
			assertEquals(0, engine.getActiveHandContextIndex());
			assertEquals(EngineState.SHOWDOWN, engine.getState());
			handler.getOut().println("You have completed a betting round.");
		}

		@Override
		public void onBettingRoundStarted()
		{
			assertEquals(0, engine.getActiveHandContextIndex());
			assertEquals(EngineState.BETTING, engine.getState());
			handler.getOut().println("You have started a new betting round! Good luck!");
		}

		@Override
		public void onCardDealtToDealer(Card card, Hand dealerHand, boolean isFaceUp)
		{
			assertEquals(0, engine.getActiveHandContextIndex());
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

			if (handContext.getHand().getCards().size() > StandardRuleConfig.INITIAL_CARD_COUNT)
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
			assertEquals(0, engine.getActiveHandContextIndex());
			assertEquals(EngineState.DEALER_TURN, engine.getState());
			handler.getOut().printf(
				"The dealer has finished drawing. Their hand is %s.\n",
				dealerHand.toStringPretty()
			);
		}

		@Override
		public void onDrawingRoundCompletedPlayer(HandContext handContext)
		{
			assertEquals(EngineState.PLAYER_TURN, engine.getState());
			handler.getOut().printf(
				"You have completed a drawing round on hand %s.\n",
				handContext.getHand().toStringPretty()
			);
		}

		@Override
		public void onDrawingRoundStartedDealer(Hand dealerHand)
		{
			assertEquals(0, engine.getActiveHandContextIndex());
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
			assertEquals(0, engine.getActiveHandContextIndex());
			assertEquals(EngineState.RESETTING, engine.getState());
			handler.getOut().println("Thanks for playing!");
		}

		@Override
		public void onGameStarted()
		{
			assertEquals(0, engine.getActiveHandContextIndex());
			assertEquals(EngineState.START, engine.getState());
			handler.getOut().println("Welcome to the table!");
		}

		@Override
		public void onInsuranceBetOpportunityDetected(Card dealerUpCard)
		{
			assertEquals(0, engine.getActiveHandContextIndex());
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
			assertEquals(0, engine.getActiveHandContextIndex());
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
		public void onPlayerSplit(HandContext previousHand, HandContext splitHand)
		{
			Assertions.assertTrue(engine.getActiveHandContextIndex() > 0);
			assertEquals(EngineState.PLAYER_TURN, engine.getState());
			handler.getOut().printf(
				String.format(
					"Your current hand is now %s and your previous hand is now %s.\n",
					splitHand.getHand().toStringPretty(),
					previousHand.getHand().toStringPretty()
				)
			);
		}

		@Override
		public void onReset()
		{
			assertEquals(0, engine.getActiveHandContextIndex());
			assertEquals(EngineState.RESETTING, engine.getState());
			handler.getOut().println("The dealer is shuffling...");
		}

		@Override
		public void onShowdownCompleted(Hand dealerHand, HandContext handContext,
										boolean playerWon, BigDecimal playerWinnings)
		{
			assertEquals(0, engine.getActiveHandContextIndex());
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
			assertEquals(0, engine.getActiveHandContextIndex());
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

	public final BigDecimal placeRandomHandBet(BigDecimal maximumBetAmount)
	throws Exception
	{
		final var BET_AMOUNT = maximumBetAmount.multiply(BigDecimal.valueOf(Math.random()));
		engine.placeHandBet(BET_AMOUNT);
		return BET_AMOUNT;
	}

	private void start() throws InsufficientChipsException, IOException
	{
		engine = new BlackjackEngine(config, LISTENER, logFilePath, loggerName);
		engine.start();
	}
}