package com.github.kqfall1.java.blackjackEngine.controllers;

import com.github.kqfall1.java.blackjackEngine.model.cards.Card;
import com.github.kqfall1.java.blackjackEngine.model.engine.EngineState;
import com.github.kqfall1.java.blackjackEngine.model.engine.RuleConfig;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import com.github.kqfall1.java.blackjackEngine.model.interfaces.EngineListener;
import com.github.kqfall1.java.handlers.input.ConsoleHandler;
import com.github.kqfall1.java.managers.InputManager;
import java.io.IOException;

/**
 * Controls a {@code GameEngine}, a {@code ConsoleHandler}, and an
 * {@code InputManager} to coordinate {@code Player} with the engine and handle
 * {@code Player} I/O synchronously.
 *
 * <p>
 * Also implements {@code EngineListener} to define app-related logic to execute
 * when internal {@code GameEngine} events occur.
 * </p>
 *
 * @author kqfall1
 * @since 15/12/2025
 */
public final class ConsoleBlackjackController implements EngineListener
{
	private final GameEngine engine;
	private final ConsoleHandler handler;
	private final InputManager inputManager;
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.GameEngine";
	private static final String LOG_FILE_PATH = "src/main/resources/logs/GameEngine.log";

	private ConsoleBlackjackController(RuleConfig config, ConsoleHandler handler,
									   String logFilePath, String loggerName)
	throws InsufficientChipsException, IOException
	{
		assert config != null : "config == null";
		assert handler != null : "handler == null";
		assert logFilePath != null && !logFilePath.isBlank()
			: "logFilePath == null || logFilePath.isBlank()";
		assert loggerName != null && !loggerName.isBlank()
			: "loggerName == null || loggerName.isBlank()";
		engine = new GameEngine(config, this, logFilePath, loggerName);
		this.handler = handler;
		inputManager = new InputManager(handler, handler, handler);
	}

	private GameEngine getEngine()
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
		final var config = new RuleConfig();
		final var handler = new ConsoleHandler();
		final var controller = new ConsoleBlackjackController(config, handler,
			LOG_FILE_PATH, LOGGER_NAME);
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
		getHandler().getOut().printf(
			"The dealer was dealt %s.",
			card
		);
	}

	@Override
	public void onCardDealtToPlayer(Card card)
	{
		getHandler().getOut().printf(
			"You were dealt %s.",
			card
		);
	}

	@Override
	public void onDrawingRoundCompletedDealer()
	{
		getHandler().getOut().print("The dealer has finished drawing.");
	}

	@Override
	public void onDrawingRoundCompletedPlayer()
	{
		getHandler().getOut().print("You have completed a drawing round.");
	}

	@Override
	public void onDrawingRoundStartedDealer()
	{
		getHandler().getOut().print("The dealer has began drawing.");
	}

	@Override
	public void onDrawingRoundStartedPlayer()
	{
		getHandler().getOut().print("You have began a new drawing round.");
	}

	@Override
	public void onGameCompleted()
	{
		getHandler().getOut().print("Thanks for playing!");
	}

	@Override
	public void onGameStarted()
	{
		getHandler().getOut().print("Welcome to the casino!");
	}

	@Override
	public void onInsuranceBetOpportunityDetected()
	{
		getHandler().getOut().println("You are eligible to place an insurance side bet.");
	}

	@Override
	public void onInsuranceBetResolved(boolean playerWon)
	{
		if (playerWon)
		{
			getHandler().getOut().println("You have won your insurance bet.");
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
	public void onShowdownCompleted(boolean playerWon)
	{
		if (playerWon)
		{
			getHandler().getOut().println("You have won a showdown.");
		}
		else
		{
			getHandler().getOut().println("You have lost a showdown.");
		}
	}

	@Override
	public void onShowdownStarted()
	{
		getHandler().getOut().println("You have began a showdown.");
	}

	@Override
	public void onStateChanged(EngineState oldState) {}

	private void performAction()
	{

	}

	private void placeBet()
	{

	}

	private void placeInsuranceBet()
	{

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
				//default ??
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