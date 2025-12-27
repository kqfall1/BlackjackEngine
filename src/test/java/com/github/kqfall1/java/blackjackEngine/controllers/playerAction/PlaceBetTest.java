package com.github.kqfall1.java.blackjackEngine.controllers.playerAction;

import com.github.kqfall1.java.blackjackEngine.controllers.EngineTestTemplate;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import java.io.IOException;
import java.math.BigDecimal;

public final class PlaceBetTest extends EngineTestTemplate
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
		super.placeRandomHandBet(PREVIOUS_CHIP_AMOUNT);

		Assertions.assertTrue(PREVIOUS_CHIP_AMOUNT.compareTo(super.engine.getPlayer().getChips()) > 0);
		Assertions.assertNotNull(super.engine.getActiveHandContext().getBet());
		Assertions.assertNotNull(super.engine.getActiveHandContext().getPot());
		Assertions.assertEquals(
			super.engine.getActiveHandContext().getBet().getAmount().multiply(BigDecimal.TWO).stripTrailingZeros(),
			super.engine.getActiveHandContext().getPot().getAmount()
		);
	}
}