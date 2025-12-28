package com.github.kqfall1.java.blackjackEngine.controllers.playerAction;

import com.github.kqfall1.java.blackjackEngine.controllers.EngineTestTemplate;
import com.github.kqfall1.java.blackjackEngine.model.engine.EngineState;
import com.github.kqfall1.java.blackjackEngine.model.engine.StandardRuleConfig;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class DoubleDownTest extends EngineTestTemplate
{
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/DoubleDownTest.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.playerAction.DoubleDownTest.log";
	private static final BigDecimal MAXIMUM_INITIAL_BET_AMOUNT = INITIAL_PLAYER_CHIP_AMOUNT.divide(
		BigDecimal.TWO,
		MathContext.DECIMAL128
	);

	@BeforeEach
	@Override
	public void init() throws InsufficientChipsException, IOException
	{
		super.initDependencies();
		super.initEngine(LOG_FILE_PATH, LOGGER_NAME);
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	public void main() throws Exception
	{
		super.advanceToPlayerTurn(MAXIMUM_INITIAL_BET_AMOUNT);

		if (super.engine.getState() == EngineState.PLAYER_TURN)
		{
			Assertions.assertFalse(super.engine.getActiveHandContext().isAltered());
			super.engine.playerDoubleDown();
			Assertions.assertTrue(
				super.engine.getActiveHandContext().getHand().getCards().size() == StandardRuleConfig.INITIAL_CARD_COUNT + 1
				&& super.engine.getActiveHandContext().isAltered()
			);
			super.engine.advanceAfterPlayerTurn();
			super.advanceToEndAfterPotentialDealerTurn();
		}
	}
}