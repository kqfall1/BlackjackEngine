package com.github.kqfall1.java.blackjackEngine.controllers;

import com.github.kqfall1.java.blackjackEngine.model.engine.EngineState;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import java.io.IOException;

/**
 * Tests the {@code BlackjackEngine} hitting mechanism.
 *
 * @author kqfall1
 * @since 22/12/2025
 */
final class HitTest extends EngineTestTemplate
{
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/HitTest.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.HitTest.log";

	@BeforeEach
	@Override
	void init() throws InsufficientChipsException, IOException
	{
		super.logFilePath = LOG_FILE_PATH;
		super.loggerName = LOGGER_NAME;
		super.init();
		super.start();
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	void main() throws Exception
	{
		final var PREVIOUS_CHIP_AMOUNT = super.engine.getPlayer().getChips();
		super.engine.placeHandBet(PREVIOUS_CHIP_AMOUNT);
		super.advanceToPlayerTurn();

		if (super.engine.getState() == EngineState.PLAYER_TURN)
		{
			Assertions.assertTrue(
				super.engine.getPlayer().getChips().compareTo(PREVIOUS_CHIP_AMOUNT) < 0
			);

			int previousCardCount;
			while (super.engine.getState() == EngineState.PLAYER_TURN)
			{
				previousCardCount = super.engine.getActiveHandContext().getHand().getCards().size();
				super.engine.playerHit();

				if (super.engine.getState() == EngineState.PLAYER_TURN)
				{
					Assertions.assertTrue(
						super.engine.getActiveHandContext().getHand().getCards().size() > previousCardCount);
				}
			}
		}
	}
}