package com.github.kqfall1.java.blackjackEngine.controllers.playerAction.splitting;

import com.github.kqfall1.java.blackjackEngine.controllers.CustomDeckTest;
import com.github.kqfall1.java.blackjackEngine.model.engine.EngineState;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Tests the {@code BlackjackEngine} splitting and surrendering mechanisms together.
 *
 * @author kqfall1
 * @since 25/12/2025
 */
final class SplitAndSurrenderTest extends CustomDeckTest
{
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/SplitAndSurrenderTest.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.playerAction.splitting.SplitAndSurrenderTest.log";
	private static final BigDecimal MAXIMUM_INITIAL_BET_AMOUNT = INITIAL_PLAYER_CHIP_AMOUNT.divide(
		BigDecimal.valueOf(MAXIMUM_SPLIT_COUNT + 2),
		RoundingMode.HALF_UP
	);

	@BeforeEach
	@Override
	public void init() throws InsufficientChipsException, IOException
	{
		super.logFilePath = LOG_FILE_PATH;
		super.loggerName = LOGGER_NAME;
		super.initCardsForSplitting7s();
		super.init();
		super.config.setPlayerCanSurrenderOnSplitHands(true);
		super.config.setMaximumSplitCount(MAXIMUM_SPLIT_COUNT);
		super.start(testDeck);
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	public void main() throws Exception
	{
		super.placeHandBet(MAXIMUM_INITIAL_BET_AMOUNT);
		super.advanceToPlayerTurn();

		if (super.engine.getState() == EngineState.PLAYER_TURN)
		{
			super.initSplitHands();

			while (super.engine.getState() == EngineState.PLAYER_TURN)
			{
				super.engine.playerSurrender();
			}
		}
	}
}