package com.github.kqfall1.java.blackjackEngine.controllers.playerAction;

import com.github.kqfall1.java.blackjackEngine.controllers.EngineTestTemplate;
import com.github.kqfall1.java.blackjackEngine.model.engine.EngineState;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Tests the {@code BlackjackEngine} doubling-down mechanism.
 *
 * @author kqfall1
 * @since 23/12/2025
 */
final class DoubleDownTest extends EngineTestTemplate
{
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/DoubleDownTest.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.playerAction.DoubleDownTest.log";
	private static final BigDecimal MAXIMUM_INITIAL_BET_AMOUNT = INITIAL_PLAYER_CHIP_AMOUNT.divide(
		BigDecimal.TWO,
		RoundingMode.HALF_UP
	);

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
		super.placeHandBet(super.randomBetAmount(MAXIMUM_INITIAL_BET_AMOUNT));
		super.advanceToPlayerTurn();

		if (super.engine.getState() == EngineState.PLAYER_TURN)
		{
			Assertions.assertTrue(super.engine.getPlayer().getChips().compareTo(PREVIOUS_CHIP_AMOUNT) < 0);
			Assertions.assertFalse(super.engine.getActiveHandContext().isAltered());
			super.engine.playerDoubleDown();
		}
	}
}