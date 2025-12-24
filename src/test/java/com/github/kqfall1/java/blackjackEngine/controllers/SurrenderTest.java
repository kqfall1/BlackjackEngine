package com.github.kqfall1.java.blackjackEngine.controllers;

import com.github.kqfall1.java.blackjackEngine.model.engine.EngineState;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import java.io.IOException;

/**
 * Tests the {@code BlackjackEngine} surrendering mechanism.
 *
 * @author kqfall1
 * @since 23/12/2025
 */
final class SurrenderTest extends EngineTestTemplate
{
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/SurrenderTest.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.SurrenderTest.log";

	@BeforeEach
	@Override
	void init() throws InsufficientChipsException, IOException
	{
		super.init();
		engine = new BlackjackEngine(config, LISTENER, LOG_FILE_PATH, LOGGER_NAME);
		engine.start();
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	void main() throws Exception
	{
		final var PREVIOUS_CHIP_AMOUNT = engine.getPlayer().getChips();
		engine.placeHandBet(DEFAULT_BET_AMOUNT);
		super.deal();

		if (engine.getState() == EngineState.PLAYER_TURN)
		{
			Assertions.assertEquals(
				PREVIOUS_CHIP_AMOUNT.subtract(DEFAULT_BET_AMOUNT),
				engine.getPlayer().getChips()
			);
			Assertions.assertFalse(engine.getActiveHandContext().isAltered());

			final var SURRENDER_AMOUNT = engine.getActiveHandContext().getBet().getHalf();
			engine.playerSurrender();

			Assertions.assertEquals(
				PREVIOUS_CHIP_AMOUNT.subtract(SURRENDER_AMOUNT),
				engine.getPlayer().getChips()
			);
		}
	}
}