package com.github.kqfall1.java.blackjackEngine.controllers.playerAction.splitting;

import com.github.kqfall1.java.blackjackEngine.controllers.CustomDeckTest;
import com.github.kqfall1.java.blackjackEngine.model.cards.Rank;
import com.github.kqfall1.java.blackjackEngine.model.engine.EngineState;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

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

	@BeforeEach
	@Override
	public void init() throws InsufficientChipsException, IOException
	{
		super.initCardsForSplitting(Rank.SIX);
		super.initDependencies();
		super.config.setSurrenderingAllowed(true);
		super.config.setSurrenderingOnSplitHandsAllowed(true);
		super.config.setMaximumSplitCount(MAXIMUM_SPLIT_COUNT);
		super.initEngine(LOG_FILE_PATH, LOGGER_NAME);
		super.engine.getDealer().setCardSource(testDeck);
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	public void main() throws Exception
	{
		super.advanceToPlayerTurn(SPLIT_TEST_MAXIMUM_INITIAL_BET_AMOUNT);

		if (super.engine.getState() == EngineState.PLAYER_TURN)
		{
			super.initSplitHands();

			while (super.engine.getState() == EngineState.PLAYER_TURN)
			{
				for (int count = 0
				 	; count < super.engine.getConfig().getMaximumSplitCount() + 1
				 	; count++)
				{
					super.engine.playerSurrender();
				}

				super.engine.advanceAfterPlayerTurn();
			}
		}

		super.advanceToEndOfRound();
	}
}