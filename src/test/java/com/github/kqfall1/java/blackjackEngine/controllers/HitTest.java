package com.github.kqfall1.java.blackjackEngine.controllers;

import com.github.kqfall1.java.blackjackEngine.model.engine.EngineState;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import java.io.IOException;

final class HitTest extends EngineTestTemplate
{
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/HitTest.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.HitTest.log";

	@RepeatedTest(TEST_ITERATIONS)
	void hitTest() throws Exception
	{
		final var previousChipAmount = engine.getPlayer().getChips();
		engine.placeHandBet(DEFAULT_BET);

		if (engine.getState() == EngineState.DEALING)
		{
			Assertions.assertTrue(previousChipAmount.compareTo(engine.getPlayer().getChips()) > 0);
			engine.declineInsuranceBet();
		}

		if (engine.getState() == EngineState.PLAYER_TURN)
		{
			Assertions.assertTrue(previousChipAmount.compareTo(engine.getPlayer().getChips()) > 0);

			int previousCardCount;
			while (engine.getState() == EngineState.PLAYER_TURN)
			{
				previousCardCount = engine.getActiveHandContext().getHand().getCards().size();
				engine.playerHit();

				if (engine.getState() != EngineState.PLAYER_TURN)
				{
					break;
				}

				Assertions.assertTrue(
					engine.getActiveHandContext().getHand().getCards().size() > previousCardCount);
			}
		}
	}

	@BeforeEach
	void init() throws InsufficientChipsException, IOException
	{
		super.init();
		engine = new BlackjackEngine(config, LISTENER, LOG_FILE_PATH, LOGGER_NAME);
		engine.start();
	}
}