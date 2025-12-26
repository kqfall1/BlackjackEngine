package com.github.kqfall1.java.blackjackEngine.controllers.dealerTurn;

import com.github.kqfall1.java.blackjackEngine.controllers.CustomDeckTest;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import java.io.IOException;

final class DealerHitAndStandTest extends CustomDeckTest
{
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/DealerHitAndStandTest.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.dealerTurn.DealerHitAndStandTest.log";

	@BeforeEach
	@Override
	public void init() throws InsufficientChipsException, IOException
	{
		super.logFilePath = LOG_FILE_PATH;
		super.loggerName = LOGGER_NAME;
		super.initCardsForDealerWin();
		super.init();
		super.start(testDeck);
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	public void main() throws Exception
	{
		final var PREVIOUS_CHIP_AMOUNT = super.engine.getPlayer().getChips();
		super.advanceToDealerTurn();
		Assertions.assertTrue(super.engine.getPlayer().getChips().compareTo(PREVIOUS_CHIP_AMOUNT) < 0);
	}
}