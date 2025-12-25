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
	private static final int MAXIMUM_SPLIT_COUNT = 2;
	private static final BigDecimal MAXIMUM_INITIAL_BET_AMOUNT = INITIAL_PLAYER_CHIP_AMOUNT.divide(
		BigDecimal.valueOf(MAXIMUM_SPLIT_COUNT + 2),
		RoundingMode.HALF_UP
	);

	@BeforeEach
	@Override
	void init() throws InsufficientChipsException, IOException
	{
		super.logFilePath = LOG_FILE_PATH;
		super.loggerName = LOGGER_NAME;
		super.initCardsForSplitting7s();
		super.init();
		super.config.setMaximumSplitCount(MAXIMUM_SPLIT_COUNT);
		super.start(testDeck);
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	void main() throws Exception
	{
		super.placeHandBet(MAXIMUM_INITIAL_BET_AMOUNT);
		var previousChipAmount = super.engine.getPlayer().getChips();
		final var BET_AMOUNT = super.engine.getActiveHandContext().getBet().getAmount();
		super.advanceToPlayerTurn();

		if (super.engine.getState() == EngineState.PLAYER_TURN)
		{
			for (int count = 0; count < MAXIMUM_SPLIT_COUNT; count++)
			{
				Assertions.assertFalse(super.engine.getActiveHandContext().isAltered());
				Assertions.assertTrue(super.engine.getActiveHandContext().getHand().isPocketPair());
				super.engine.playerSplit();

				Assertions.assertEquals(
					previousChipAmount.subtract(BET_AMOUNT),
					super.engine.getPlayer().getChips()
				);

				previousChipAmount = super.engine.getPlayer().getChips();
			}

			for (int count = 0; count < MAXIMUM_SPLIT_COUNT + 1; count++)
			{
				super.engine.playerStand();
			}
		}
	}
}