package com.github.kqfall1.java.blackjackEngine.controllers.config;

import com.github.kqfall1.java.blackjackEngine.controllers.CustomDeckTest;
import com.github.kqfall1.java.blackjackEngine.model.enums.Rank;
import com.github.kqfall1.java.blackjackEngine.model.enums.EngineState;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.IllegalHandOperationException;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class MaximumSplitCountTest extends CustomDeckTest
{
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/MaximumSplitCountTest.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.config.MaximumSplitCountTest.log";

	@BeforeEach
	@Override
	public void init() throws InsufficientChipsException, IOException
	{
		super.initCardsForSplitting(Rank.ACE);
		super.initDependencies();
		super.config.setMaximumSplitCount(
			ThreadLocalRandom.current().nextInt(MAXIMUM_SPLIT_COUNT + 1)
		);
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
			try
			{
				super.initSplitHands();

				for (int count = 0; count < MAXIMUM_SPLIT_COUNT + 1; count++)
				{
					super.engine.playerStand();
				}

				super.engine.advanceAfterPlayerTurn();
				super.advanceToEndOfRound();
			}
			catch (IllegalHandOperationException e)
			{
				System.out.println(e.getMessage());
			}
		}
	}
}