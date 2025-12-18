package com.github.kqfall1.java.blackjackEngine.controllers;

import com.github.kqfall1.java.blackjackEngine.model.cards.Card;
import com.github.kqfall1.java.blackjackEngine.model.engine.EngineState;
import com.github.kqfall1.java.blackjackEngine.model.engine.StandardRuleConfig;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import com.github.kqfall1.java.blackjackEngine.model.interfaces.EngineListener;
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
 * Also implements {@code EngineListener} to define app-related logic to execute
 * when internal {@code BlackjackEngine} events occur.
 * </p>
 *
 * @author kqfall1
 * @since 15/12/2025
 */
public final class ConsoleBlackjackController implements EngineListener
{
	private final BlackjackEngine engine;
	private final ConsoleHandler handler;
	private final InputManager inputManager;
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.BlackjackEngine";
	private static final String LOG_FILE_PATH = "src/main/resources/logs/BlackjackEngine.log";
	private static final BigDecimal PLAYER_INITIAL_CHIPS = BigDecimal.valueOf(5000);

	private ConsoleBlackjackController(StandardRuleConfig config, ConsoleHandler handler,
									   String logFilePath, String loggerName)
	throws InsufficientChipsException, IOException
	{
		assert config != null : "config == null";
		assert handler != null : "handler == null";
		assert logFilePath != null && !logFilePath.isBlank()
			: "logFilePath == null || logFilePath.isBlank()";
		assert loggerName != null && !loggerName.isBlank()
			: "loggerName == null || loggerName.isBlank()";
		engine = new BlackjackEngine(config, this, logFilePath, loggerName);
		this.handler = handler;
		inputManager = new InputManager(handler, handler, handler);
	}

	private BlackjackEngine getEngine()
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
		final var config = new StandardRuleConfig();
		config.setPlayerInitialChips(PLAYER_INITIAL_CHIPS);
		final var handler = new ConsoleHandler();
		final var controller = new ConsoleBlackjackController(config, handler,
			LOG_FILE_PATH, LOGGER_NAME);
		controller.getEngine().getLogger().setLevel(Level.FINE);
		controller.run();
	}

	@Override
	public void onBetPlaced()
	{
		getHandler().getOut().printf(
			"You placed a bet of $%.2f.\n",
			getEngine().getActivePlayerHand().getBet().getAmount()
		);
	}

	@Override
	public void onBettingRoundCompleted() {}

	@Override
	public void onBettingRoundStarted()
	{
		getHandler().getOut().println("Good luck!");
	}

	@Override
	public void onCardDealtToDealer(Card card)
	{
		if (getEngine().getDealer().getHand().getCards().size() ==
			StandardRuleConfig.INITIAL_CARD_COUNT)
		{
			getHandler().getOut().printf(
				"The dealer is showing the %s.\n",
				card.toStringPretty()
			);
		}
		else if (getEngine().getDealer().getHand().getCards().size() !=
			StandardRuleConfig.INITIAL_CARD_COUNT - 1)
		{
			getHandler().getOut().printf(
				"The dealer was dealt the %s.\n",
				card.toStringPretty()
			);
		}
	}

	@Override
	public void onCardDealtToPlayer(Card card)
	{
		getHandler().getOut().printf(
			"You were dealt the %s.\n",
			card.toStringPretty()
		);
	}

	@Override
	public void onDrawingRoundCompletedDealer()
	{
		getHandler().getOut().println("The dealer has finished drawing.");
	}

	@Override
	public void onDrawingRoundCompletedPlayer()
	{
		getHandler().getOut().println("You have completed a drawing round.");
	}

	@Override
	public void onDrawingRoundStartedDealer()
	{
		getHandler().getOut().println("The dealer has began drawing.");
	}

	@Override
	public void onDrawingRoundStartedPlayer()
	{
		getHandler().getOut().println("You have began a new drawing round.");
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
	public void onInsuranceBetOpportunityDetected()
	{
		getHandler().getOut().println("You are eligible to place an insurance side bet.");
	}

	@Override
	public void onInsuranceBetResolved(BigDecimal playerWinnings)
	{
		if (playerWinnings.compareTo(BigDecimal.ZERO) > 0)
		{
			getHandler().getOut().printf(
				"You have won your insurance bet and collect $%.2f.",
				playerWinnings
			);
		}
		else
		{
			getHandler().getOut().println("You have lost your insurance bet.");
		}
	}

	@Override
	public void onReset()
	{
		getHandler().getOut().println("The dealer is shuffling...");
	}

	@Override
	public void onShowdownCompleted(boolean playerWon, BigDecimal playerWinnings)
	{
		final var completedString = String.format(
			"Your score is %d and the dealer's score is %d.",
			getEngine().getActivePlayerHand().getHand().getScore(),
			getEngine().getDealer().getHand().getScore()
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
				"%s You have lost the showdown, yet still collect $%.2f.\n",
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
	public void onShowdownStarted()
	{
		getHandler().getOut().printf(
			"You have began a showdown. The dealer's down card is the %s.\n",
			getEngine().getDealer().getHand().getCards().getFirst().toStringPretty()
		);
	}

	@Override
	public void onStateChanged(EngineState oldState) {}

	private void performAction()
	{
		getInputManager().getStringInputter().getString(
			String.format(
				"Your score is %d. Enter 'd' to double down, 'h' to hit, 'sp' to split, 'st' to stand, 'su' to surrender",
				getEngine().getActivePlayerHand().getHand().getScore()
			),
			new String[] {"d", "h", "sp", "st", "su"}
		).thenAccept(input -> {
			try
			{
				switch (input)
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
				performAction();
			}
		});
	}

	private void placeBet()
	{
		getInputManager().getNumberInputter().getNumber(
			String.format(
				"You have $%.2f. Please place a bet",
				getEngine().getPlayer().getChips()
			),
			Float.MIN_VALUE,
			Float.MAX_VALUE
		).thenAccept(amount -> {
			try
			{
				getEngine().placeBet(BigDecimal.valueOf(amount));
			}
			catch (Exception e)
			{
				getHandler().showException(e);
				placeBet();
			}
		});
	}

	private void placeInsuranceBet()
	{
		getInputManager().getYesNoInputter().getYesNo("Do you wish to place an insurance bet?")
		.thenAccept(answer -> {
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
				placeInsuranceBet();
			}
		});
	}

	private void run()
	{
		while (getEngine().getState() != EngineState.END)
		{
			switch (getEngine().getState())
			{
				case EngineState.START, EngineState.BETTING -> placeBet();
				case EngineState.INSURANCE_CHECK -> placeInsuranceBet();
				case EngineState.PLAYER_TURN -> performAction();
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