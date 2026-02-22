package com.github.kqfall1.java.blackjackEngine.playerAction;

import com.github.kqfall1.java.blackjackEngine.engine.EngineTest;
import com.github.kqfall1.java.blackjackEngine.model.engine.BlackjackConstants;
import com.github.kqfall1.java.blackjackEngine.model.enums.EngineState;

import java.math.BigDecimal;
import java.math.MathContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class DoubleDownTest extends EngineTest
{
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/DoubleDownTest.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.playerAction.DoubleDownTest.log";
	private static final BigDecimal MAXIMUM_INITIAL_BET_AMOUNT = INITIAL_PLAYER_CHIP_AMOUNT.divide(
		BigDecimal.TWO,
		MathContext.DECIMAL128
	);

	@BeforeEach
	@Override
	public void init() {
		super.initDependencies();
		super.initEngine(LOG_FILE_PATH, LOGGER_NAME);
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	public void main()
	{
		final var INITIAL_BET_AMOUNT = super.advanceToPlayerTurn(MAXIMUM_INITIAL_BET_AMOUNT);

		if (super.engine.getState() == EngineState.PLAYER_TURN)
		{
			Assertions.assertFalse(super.engine.getActiveHandContext().isAltered());
			super.engine.playerDoubleDown();
			Assertions.assertTrue(
				nearlyEquals(
					INITIAL_BET_AMOUNT
						.multiply(BigDecimal.valueOf(4))
						.stripTrailingZeros(),
					super.engine.getActiveHandContext().getPot().getAmount(),
					BlackjackConstants.DEFAULT_CHIP_SCALE
				)
			);
			Assertions.assertTrue(
				super.engine.getActiveHandContext().getHand().getCards().size()
					== BlackjackConstants.INITIAL_CARD_COUNT + 1
				&& super.engine.getActiveHandContext().isAltered()
			);

			super.engine.advanceAfterPlayerTurn();
		}

		super.advanceToEndOfRound();
	}
}