package com.github.kqfall1.java.blackjackEngine.controllers;

import com.github.kqfall1.java.blackjackEngine.model.cards.Card;
import com.github.kqfall1.java.blackjackEngine.model.engine.StandardBlackjackRuleset;
import com.github.kqfall1.java.blackjackEngine.model.engine.BlackjackRulesetConfiguration;
import com.github.kqfall1.java.blackjackEngine.model.enums.EngineState;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import com.github.kqfall1.java.blackjackEngine.model.hands.Hand;
import com.github.kqfall1.java.blackjackEngine.model.hands.HandContext;
import com.github.kqfall1.java.blackjackEngine.model.interfaces.BlackjackEngineListener;
import com.github.kqfall1.java.blackjackEngine.model.interfaces.BlackjackRuleset;
import com.github.kqfall1.java.enums.YesNoInput;
import com.github.kqfall1.java.handlers.input.ConsoleHandler;
import com.github.kqfall1.java.managers.InputManager;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.logging.Level;

/**
 * Controls a {@code BlackjackEngine}, a {@code ConsoleHandler}, and an
 * {@code InputManager} to coordinate {@code Player} with the engine and handle
 * {@code Player} I/O synchronously.
 *
 * <p>
 * Also implements {@code BlackjackEngineListener} to define app-related logic to execute
 * when internal {@code BlackjackEngine} events occur.
 * </p>
 *
 * @author kqfall1
 * @since 15/12/2025
 */
public final class ConsoleBlackjackController implements BlackjackEngineListener
{
	private final BlackjackEngine engine;
	private final ConsoleHandler handler;
	private final InputManager inputManager;
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.BlackjackEngine";
	private static final String LOG_FILE_PATH = "src/main/resources/logs/BlackjackEngine.log";
	private static final BigDecimal PLAYER_INITIAL_CHIPS = BigDecimal.valueOf(5000);

	ConsoleBlackjackController(ConsoleHandler handler, String logFilePath, String loggerName,
							   BlackjackRuleset ruleset)
	throws InsufficientChipsException, IOException
	{
		assert handler != null : "handler == null";
		assert logFilePath != null && !logFilePath.isBlank()
			: "logFilePath == null || logFilePath.isBlank()";
		assert loggerName != null && !loggerName.isBlank()
			: "loggerName == null || loggerName.isBlank()";
		assert ruleset != null : "ruleset == null";
		this.handler = handler;
		inputManager = new InputManager(handler, handler, handler);
		engine = new BlackjackEngine(this, logFilePath, loggerName, ruleset);
	}

	BlackjackEngine getEngine()
	{
		return engine;
	}

	private ConsoleHandler getHandler()
	{
		return handler;
	}

	private InputManager getInputManager()
	{
		return inputManager;
	}

	public static void main(String[] args)
	throws InsufficientChipsException, IOException
	{
		final var config = new BlackjackRulesetConfiguration();
		final var handler = new ConsoleHandler();
		config.setShouldDealerHitOnSoft17(true);
		config.setDoublingDownOnSplitHandsAllowed(true);
		config.setSurrenderingOnSplitHandsAllowed(true);
		config.setPlayerInitialChips(PLAYER_INITIAL_CHIPS);
		//SET MORE PARAMETERS HERE
		final var ruleset = new StandardBlackjackRuleset(config);
		final var controller = new ConsoleBlackjackController(
			handler,
			LOG_FILE_PATH,
			LOGGER_NAME,
			ruleset
		);
		controller.getEngine().getLogger().setLevel(Level.FINE);
		controller.getEngine().start();
	}

	@Override
	public void onBetPlaced(HandContext handContext)
	{
		getHandler().getOut().printf(
			"You placed a bet of $%.2f.\n",
			handContext.getBet().getAmount()
		);

		try
		{
			getEngine().deal();
		}
		catch (InsufficientChipsException ignored) {}
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
			getHandler().getOut().printf(
				"The dealer was dealt the %s.\n",
				card.toStringPretty()
			);
		}
	}

	@Override
	public void onCardDealtToPlayer(Card card, HandContext handContext)
	{
		getHandler().getOut().printf(
			"You were dealt the %s. Your current hand is now %s.\n",
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
	public void onDrawingRoundCompletedPlayer(HandContext handContext)
	{
		getHandler().getOut().printf(
			"You have completed a drawing round on hand %s.\n",
			handContext.getHand().toStringPretty()
		);
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
		getHandler().getOut().printf(
			"The dealer is showing the %s. You are eligible to place an insurance side bet.\n",
			dealerUpCard.toStringPretty()
		);
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
	public void onPlayerSplit(HandContext previousHand, HandContext splitHand)
	{
		getHandler().getOut().printf(
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
		getHandler().getOut().println("The dealer is shuffling...");
	}

	@Override
	public void onShowdownCompleted(Hand dealerHand, HandContext handContext,
									boolean playerWon, BigDecimal playerWinnings)
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
	public void onStateChanged(EngineState oldState)
	{
		switch (getEngine().getState())
		{
			case BETTING -> placeHandBet();
			case END -> { System.exit(0); }
			case INSURANCE_CHECK -> placeInsuranceBet();
			case PLAYER_TURN -> performAction();
		}
	}

	private void performAction()
	{
		if (getEngine().getState() != EngineState.PLAYER_TURN)
		{
			return;
		}

		final var selection = getInputManager().getStringInputter().getString(
			String.format(
				"Your score is %d. Enter 'd' to double down, 'h' to hit, 'sp' to split, 'st' to stand, 'su' to surrender",
				getEngine().getActiveHandContext().getHand().getScore()
			),
			new String[] {"d", "h", "sp", "st", "su"}
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
		catch (Exception e)
		{
			getHandler().showException(e);

			if (getEngine().getState() != EngineState.END)
			{
				performAction();
			}
		}
	}

	private void placeHandBet()
	{
		if (getEngine().getState() != EngineState.BETTING)
		{
			return;
		}

		final var amount = getInputManager().getNumberInputter().getNumber(
			String.format(
				"You have $%.2f. Please place a bet",
				getEngine().getPlayer().getChips()
			),
			Float.MIN_VALUE,
			Float.MAX_VALUE
		).join();

		try
		{
			getEngine().placeHandBet(BigDecimal.valueOf(amount));
		}
		catch (Exception e)
		{
			getHandler().showException(e);

			if (getEngine().getState() != EngineState.END)
			{
				placeHandBet();
			}
		}
	}

	private void placeInsuranceBet()
	{
		if (getEngine().getState() != EngineState.INSURANCE_CHECK)
		{
			return;
		}

		final var answer = getInputManager().getYesNoInputter().getYesNo(
			"Do you wish to place an insurance bet?"
		).join();

		try
		{
			if (answer == YesNoInput.YES)
			{
				getEngine().acceptInsuranceBet();
			}
			else
			{
				getEngine().declineInsuranceBet();
			}
		}
		catch (Exception e)
		{
			getHandler().showException(e);

			if (getEngine().getState() != EngineState.END)
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