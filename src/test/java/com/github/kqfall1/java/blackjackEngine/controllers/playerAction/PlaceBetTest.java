package com.github.kqfall1.java.blackjackEngine.controllers.playerAction;

import com.github.kqfall1.java.blackjackEngine.controllers.EngineTest;
import com.github.kqfall1.java.blackjackEngine.model.engine.EngineState;
import com.github.kqfall1.java.blackjackEngine.model.engine.StandardRuleConfig;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import java.io.IOException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

public final class PlaceBetTest extends EngineTest
{
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/PlaceBetTest.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.playerAction.PlaceBetTest.log";

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
		final var PREVIOUS_CHIP_AMOUNT = super.engine.getPlayer().getChips();
		final var BET_AMOUNT = super.advanceToPlayerTurn(PREVIOUS_CHIP_AMOUNT);

		if (super.engine.getState() == EngineState.PLAYER_TURN)
		{
			super.engine.playerStand();
			super.engine.advanceAfterPlayerTurn();

			Assertions.assertTrue(
				PREVIOUS_CHIP_AMOUNT.compareTo(
					super.engine.getPlayer().getChips()) > 0
			);
			Assertions.assertNotNull(super.engine.getActiveHandContext().getBet());
			Assertions.assertNotNull(super.engine.getActiveHandContext().getPot());
			Assertions.assertTrue(
				nearlyEquals(
					BET_AMOUNT.stripTrailingZeros(),
					super.engine.getActiveHandContext().getBet().getAmount(),
					StandardRuleConfig.CHIP_SCALE
				)
			);
			Assertions.assertTrue(
				nearlyEquals(
					BET_AMOUNT.multiply(BigDecimal.TWO).stripTrailingZeros(),
					super.engine.getActiveHandContext().getPot().getAmount(),
					StandardRuleConfig.CHIP_SCALE
				)
			);
		}

		super.advanceToEndOfRound();
	}
}