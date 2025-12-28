package com.github.kqfall1.java.blackjackEngine.controllers.playerAction.splitting;

import com.github.kqfall1.java.blackjackEngine.controllers.CustomDeckTest;
import com.github.kqfall1.java.blackjackEngine.model.engine.EngineState;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class SplitAndDoubleDownTest extends CustomDeckTest
{
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/SplitAndDoubleDownTest.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.playerAction.splitting.SplitAndDoubleDownTest.log";
	private static final BigDecimal MAXIMUM_INITIAL_BET_AMOUNT = INITIAL_PLAYER_CHIP_AMOUNT.divide(
		BigDecimal.valueOf((CustomDeckTest.MAXIMUM_SPLIT_COUNT + 2) * 2),
		MathContext.DECIMAL128
	);

	@BeforeEach
	@Override
	public void init() throws InsufficientChipsException, IOException
	{
		super.initCardsForSplitting7s();
		super.initDependencies();
		super.config.setPlayerCanDoubleDownOnSplitHands(true);
		super.config.setMaximumSplitCount(MAXIMUM_SPLIT_COUNT);
		super.initEngine(LOG_FILE_PATH, LOGGER_NAME);
		super.engine.getDealer().setDeck(testDeck);
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	public void main() throws Exception
	{
		super.advanceToPlayerTurn(MAXIMUM_INITIAL_BET_AMOUNT);

		if (super.engine.getState() == EngineState.PLAYER_TURN)
		{
			super.initSplitHands();

			while (!super.engine.getActiveHandContext().isAltered())
			{
				super.engine.playerDoubleDown();
			}

			super.engine.advanceAfterPlayerTurn();
			super.advanceToEndAfterPotentialDealerTurn();
		}
	}
}