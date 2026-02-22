package com.github.kqfall1.java.blackjackEngine.rules.splitting;

import com.github.kqfall1.java.blackjackEngine.engine.CustomDeckTest;
import com.github.kqfall1.java.blackjackEngine.model.enums.Rank;
import com.github.kqfall1.java.blackjackEngine.model.enums.EngineState;
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
	public void init() {
		super.initCardsForSplittingWithoutHitting(Rank.SIX);
		super.initDependencies();
		super.ruleset.getConfig().setSurrenderingAllowed(true);
		super.ruleset.getConfig().setSurrenderingOnSplitHandsAllowed(true);
		super.ruleset.getConfig().setMaximumSplitCount(MAXIMUM_SPLIT_COUNT);
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
			for (int count = 0
				 ; count < super.ruleset.getConfig().getMaximumSplitCount()
				; count++)
			{
				super.initSplitHands();
				super.engine.playerSurrender();
			}

			super.engine.playerSurrender();
		}

		super.advanceToEndOfRound();
	}
}