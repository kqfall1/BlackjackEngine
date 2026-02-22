package com.github.kqfall1.java.blackjackEngine.rules.splitting;

import com.github.kqfall1.java.blackjackEngine.engine.CustomDeckTest;
import com.github.kqfall1.java.blackjackEngine.model.enums.Rank;
import com.github.kqfall1.java.blackjackEngine.model.enums.EngineState;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.IllegalHandOperationException;

import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class MaximumSplitCountTest extends CustomDeckTest
{
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/MaximumSplitCountTest.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.config.MaximumSplitCountTest.log";

	@BeforeEach
	@Override
	public void init() {
		super.initCardsForSplittingWithoutHitting(Rank.ACE);
		super.initDependencies();
		super.ruleset.getConfig().setMaximumSplitCount(
			ThreadLocalRandom.current().nextInt(MAXIMUM_SPLIT_COUNT + 1)
		);
		super.ruleset.getConfig().setSplittingAcesAllowed(true);
		super.initEngine(LOG_FILE_PATH, LOGGER_NAME);
		super.engine.getDealer().setCardSource(testDeck);
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	public void main()
	{
		super.advanceToPlayerTurn(SPLIT_TEST_MAXIMUM_INITIAL_BET_AMOUNT);

		if (super.engine.getState() == EngineState.PLAYER_TURN)
		{
			try
			{
				for (int count = 0; count < super.ruleset.getConfig().getMaximumSplitCount(); count++)
				{
					super.initSplitHands();
					super.engine.playerStand();
				}

				super.engine.playerStand();
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