package com.github.kqfall1.java.blackjackEngine.controller;

import com.github.kqfall1.java.blackjackEngine.model.cards.Card;
import com.github.kqfall1.java.blackjackEngine.model.engine.BlackjackConstants;
import com.github.kqfall1.java.blackjackEngine.model.engine.BlackjackEngine;
import com.github.kqfall1.java.blackjackEngine.model.engine.StandardBlackjackRuleset;
import com.github.kqfall1.java.blackjackEngine.model.engine.BlackjackRulesetConfiguration;
import com.github.kqfall1.java.blackjackEngine.model.enums.BlackjackEngineState;
import com.github.kqfall1.java.blackjackEngine.model.hands.Hand;
import com.github.kqfall1.java.blackjackEngine.model.hands.HandContext;
import com.github.kqfall1.java.blackjackEngine.model.interfaces.BlackjackEngineListener;
import com.github.kqfall1.java.blackjackEngine.model.interfaces.BlackjackRuleset;
import com.github.kqfall1.java.enums.YesNoInput;
import com.github.kqfall1.java.handlers.io.ConsoleIoHandler;
import com.github.kqfall1.java.managers.InputManager;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Level;

/**
 * Controls a {@code BlackjackEngine}, a {@code ConsoleHandler}, and an
 * {@code InputManager} to coordinate {@code Player} with the engine and handle
 * {@code Player} I/O synchronously.
 *
 * <p>
 * Also implements {@code BlackjackEngineListener} to define app-related logic to execute
 * when internal {@code BlackjackEngine} events occur. This class is used for manual
 * integration testing.
 * </p>
 *
 * @author kqfall1
 * @since 15/12/2025
 */
public final class ConsoleBlackjackController implements BlackjackEngineListener
{
	private final BlackjackEngine engine;
	private final ConsoleIoHandler handler;
	private final InputManager inputManager;
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.model.engine.ConsoleBlackjackControllerEngine";
	private static final String LOG_FILE_PATH = "src/main/resources/logs/ConsoleBlackjackControllerEngine.log";
	private static final BigDecimal PLAYER_INITIAL_CHIPS = BigDecimal.valueOf(5000);

	ConsoleBlackjackController(ConsoleIoHandler handler, Optional<Path> loggerFilePath, BlackjackRuleset ruleset)
	{
		assert handler != null : "handler == null";
		assert loggerFilePath != null : "loggerFilePath == null";
		assert ruleset != null : "ruleset == null";
		this.handler = handler;
		inputManager = new InputManager(handler, handler, handler);
		engine = new BlackjackEngine(this, Optional.empty(), ruleset);
	}

	BlackjackEngine getEngine()
	{
		return engine;
	}

	private ConsoleIoHandler getHandler()
	{
		return handler;
	}

	private InputManager getInputManager()
	{
		return inputManager;
	}

	public static void main(String[] args)
	{
		final var config = new BlackjackRulesetConfiguration();
		final var handler = new ConsoleIoHandler();
		config.setLoggingEnabled(true);
		config.setSurrenderingAllowed(true);
		config.setPlayerInitialChips(PLAYER_INITIAL_CHIPS);
		final var ruleset = new StandardBlackjackRuleset(config);
		final var controller = new ConsoleBlackjackController(handler, Optional.empty(), ruleset);
		controller.getEngine().getLogger().setLevel(Level.OFF);
		controller.getEngine().start();
	}

	@Override
	public void onBetPlaced(HandContext handContext)
	{
		getHandler().getOut().printf(
			"You placed a bet of $%.2f.\n",
			handContext.getBet().getAmount()
		);
	}

	@Override
	public void onBettingRoundCompleted()
	{
		getHandler().getOut().println("You have completed a betting round.");
	}

	@Override
	public void onBettingRoundStarted()
	{
		getHandler().getOut().println("You have started a new betting round! Good luck!");
	}

	@Override
	public void onCardDealtToDealer(Card card, Hand dealerHand, boolean isFaceUp)
	{
		if (isFaceUp)
		{
			if (dealerHand.getCards().size() == BlackjackConstants.INITIAL_CARD_COUNT - 1)
			{
				getHandler().getOut().printf(
					"The dealer is showing a %s.\n",
					card.toStringPretty()
				);
			}
			else
			{
				getHandler().getOut().printf(
					"The dealer was dealt the %s.\n",
					card.toStringPretty()
				);
			}
		}
	}

	@Override
	public void onCardDealtToPlayer(Card card, HandContext handContext)
	{
		getHandler().getOut().printf(
			"You were dealt a %s. Your current hand is now %s.\n",
			card.toStringPretty(),
			handContext.getHand().toStringPretty()
		);
	}

	@Override
	public void onDrawingRoundCompletedDealer(Hand dealerHand)
	{
		getHandler().getOut().printf(
			"The dealer has finished drawing. Their hand is %s.\n",
			dealerHand.toStringPretty()
		);
	}

	@Override
	public void onDrawingRoundCompletedPlayer(HandContext handContext) {
		getHandler().getOut().printf(
			"You have completed a drawing round on hand %s.\n",
			handContext.getHand().toStringPretty()
		);
		engine.advanceAfterDrawingRoundCompletedPlayer();
	}

	@Override
	public void onDrawingRoundStartedDealer(Hand dealerHand)
	{
		getHandler().getOut().println("The dealer has begun drawing.");
	}

	@Override
	public void onDrawingRoundStartedPlayer(HandContext handContext)
	{
		getHandler().getOut().println("You have begun a new drawing round.");
	}

	@Override
	public void onGameCompleted()
	{
		getHandler().getOut().println("Thanks for playing!");
	}

	@Override
	public void onGameStarted()
	{
		getHandler().getOut().println("Welcome to the table!");
	}

	@Override
	public void onInsuranceBetOpportunityDetected(Card dealerUpCard)
	{
		getHandler().getOut().println("You are eligible to place an insurance side bet.");
	}

	@Override
	public void onInsuranceBetResolved(boolean wasSuccessful, BigDecimal playerWinnings)
	{
		if (wasSuccessful)
		{
			getHandler().getOut().printf(
				"You have won your insurance bet and collect $%.2f.\n",
				playerWinnings
			);
		}
		else
		{
			getHandler().getOut().println("You have lost your insurance bet.");
		}
	}

	@Override
	public void onPlayerSplit(HandContext currentHand, HandContext splitHand)
	{
		getHandler().getOut().printf(
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
		getHandler().getOut().println("The dealer is creating a new betting round.");
	}

	@Override
	public void onShowdownCompleted(Hand dealerHand, HandContext handContext, boolean playerWon, BigDecimal playerWinnings)
	{
		final var completedString = String.format(
			"Your score is %d and the dealer's score is %d.",
			handContext.getHand().getScore(),
			dealerHand.getScore()
		);

		if (playerWon)
		{
			getHandler().getOut().printf(
				"%s You have won the showdown and collect $%.2f.\n",
				completedString,
				playerWinnings
			);
		}
		else if (playerWinnings.compareTo(BigDecimal.ZERO) > 0)
		{
			getHandler().getOut().printf(
				"%s You did not win the showdown, yet still collect $%.2f.\n",
				completedString,
				playerWinnings
			);
		}
		else
		{
			getHandler().getOut().printf(
				"%s You have lost the showdown.\n",
				completedString
			);
		}
	}

	@Override
	public void onShowdownStarted(Hand dealerHand, HandContext handContext)
	{
		getHandler().getOut().printf(
			"Your hand %s is being shown down against the dealer's hand %s.\n",
			handContext.getHand().toStringPretty(),
			dealerHand.toStringPretty()
		);
	}

	@Override
	public void onStateChanged(BlackjackEngineState oldState)
	{
		switch (getEngine().getState())
		{
			case BETTING -> placeHandBet();
			case DEALING ->
			{
				getEngine().deal();
				getEngine().advanceAfterDeal();
			}
			case INSURANCE_CHECK -> placeInsuranceBet();
			case PLAYER_TURN -> performAction();
			case DEALER_TURN ->
			{
				getEngine().dealerTurn();
				getEngine().advanceAfterDealerTurn();
			}
			case SHOWING_DOWN ->
			{
				getEngine().showdown();
				getEngine().advanceAfterShowdown();
			}
			case RESETTING ->
			{
				getEngine().reset();
				getEngine().advanceAfterReset();
			}
			case END -> System.exit(0);
		}
	}

	private void performAction()
	{
		final var selection = getInputManager().getStringInputter().getString(
			Optional.of(String.format(
				"Your score is %d. Enter 'd' to double down, 'h' to hit, 'sp' to split, 'st' to stand, 'su' to surrender",
				getEngine().getActiveHandContext().getHand().getScore()
			)),
			Optional.of(new String[] {"d", "h", "sp", "st", "su"})
		).join();

		try
		{
			switch (selection)
			{
				case "d" -> getEngine().playerDoubleDown();
				case "h" -> getEngine().playerHit();
				case "sp" -> getEngine().playerSplit();
				case "st" -> getEngine().playerStand();
				case "su" -> getEngine().playerSurrender();
			}
		}
		catch (RuntimeException e)
		{
			getHandler().presentFailure(e.getMessage());

			if (getEngine().getState() != BlackjackEngineState.END)
			{
				performAction();
			}
		}
	}

	private void placeHandBet()
	{
		final var amount = getInputManager().getNumberInputter().getNumber(
			Optional.of(String.format(
				"You have $%.2f. Please place a bet",
				getEngine().getPlayer().getChips()
			)),
			Float.MIN_VALUE,
			Float.MAX_VALUE
		).join();

		try
		{
			getEngine().placeBet(BigDecimal.valueOf(amount));
		}
		catch (Exception e)
		{
			getHandler().presentFailure(e.getMessage());

			if (getEngine().getState() != BlackjackEngineState.END)
			{
				placeHandBet();
			}
		}
	}

	private void placeInsuranceBet()
	{
		if (getEngine().getState() != BlackjackEngineState.INSURANCE_CHECK)
		{
			return;
		}

		final var ANSWER = getInputManager().getYesNoInputter().getYesNo(Optional.of("Do you wish to place an insurance bet?")).join();

		try
		{
			var playerWinnings = BigDecimal.ZERO;

			if (ANSWER == YesNoInput.YES)
			{
				playerWinnings = getEngine().acceptInsuranceBet();
				getEngine().advanceAfterInsuranceBet(playerWinnings);
			}
			else
			{
				getEngine().declineInsuranceBet();
			}
		}
		catch (Exception e)
		{
			getHandler().presentFailure(e.getMessage());

			if (getEngine().getState() != BlackjackEngineState.END)
			{
				placeInsuranceBet();
			}
		}
	}

	@Override
	public String toString()
	{
		return String.format(
			"%s[engine=%s,handler=%s,inputManager=%s]",
			getClass().getName(),
			getEngine(),
			getHandler(),
			getInputManager()
		);
	}
}