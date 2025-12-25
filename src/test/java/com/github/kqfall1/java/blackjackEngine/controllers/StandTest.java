package com.github.kqfall1.java.blackjackEngine.controllers;

import com.github.kqfall1.java.blackjackEngine.model.engine.EngineState;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import java.io.IOException;

/**
 * Tests the {@code BlackjackEngine} standing mechanism.
 *
 * @author kqfall1
 * @since 24/12/2025
 */
final class StandTest extends EngineTestTemplate
{
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/StandTest.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.StandTest.log";

	@BeforeEach
	@Override
	public void init() throws InsufficientChipsException, IOException
	{
		super.logFilePath = LOG_FILE_PATH;
		super.loggerName = LOGGER_NAME;
		super.init();
		super.start(null);
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	public void main() throws Exception
	{
		final var PREVIOUS_CHIP_AMOUNT = super.engine.getPlayer().getChips();
		super.engine.placeHandBet(super.engine.getPlayer().getChips());
		super.advanceToPlayerTurn();

		if (super.engine.getState() == EngineState.PLAYER_TURN)
		{
			Assertions.assertTrue(super.engine.getPlayer().getChips().compareTo(PREVIOUS_CHIP_AMOUNT) < 0);
			super.engine.playerStand();
		}
	}
}