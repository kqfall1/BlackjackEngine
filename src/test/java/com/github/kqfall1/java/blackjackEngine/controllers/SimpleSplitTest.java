package com.github.kqfall1.java.blackjackEngine.controllers;

import com.github.kqfall1.java.blackjackEngine.model.engine.EngineState;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * To test the {@code BlackjackEngine} splitting and standing mechanisms together.
 *
 * @author kqfall1
 * @since 23/12/2025
 */
final class SimpleSplitTest extends SplitTest
{
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/SimpleSplitTest.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.SimpleSplitTest.log";
	private static final BigDecimal MAXIMUM_INITIAL_BET_AMOUNT = INITIAL_PLAYER_CHIP_AMOUNT.divide(BigDecimal.TWO, RoundingMode.HALF_UP);

	@BeforeEach
	@Override
	void init() throws InsufficientChipsException, IOException
	{
		super.logFilePath = LOG_FILE_PATH;
		super.loggerName = LOGGER_NAME;
		super.initCardsForPocket7s();
		super.init();
		super.start();
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	void main() throws Exception
	{
		final var PREVIOUS_CHIP_AMOUNT = super.engine.getPlayer().getChips();
		super.engine.placeHandBet(MAXIMUM_INITIAL_BET_AMOUNT);
		super.advanceToPlayerTurn();

		if (super.engine.getState() == EngineState.PLAYER_TURN)
		{
			Assertions.assertTrue(super.engine.getPlayer().getChips().compareTo(PREVIOUS_CHIP_AMOUNT) < 0);
			Assertions.assertFalse(super.engine.getActiveHandContext().isAltered());
			final var BET_AMOUNT = super.engine.getActiveHandContext().getBet().getAmount();
			super.engine.playerSplit();

			Assertions.assertEquals(
				PREVIOUS_CHIP_AMOUNT.subtract(BET_AMOUNT.multiply(BigDecimal.TWO)),
				super.engine.getPlayer().getChips()
			);

			super.engine.playerStand();
			super.engine.playerStand();
		}
	}
}