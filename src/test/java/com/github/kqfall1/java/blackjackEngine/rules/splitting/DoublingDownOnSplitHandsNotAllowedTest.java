package com.github.kqfall1.java.blackjackEngine.rules.splitting;

import com.github.kqfall1.java.blackjackEngine.engine.CustomDeckTest;
import com.github.kqfall1.java.blackjackEngine.model.enums.BlackjackEngineState;
import com.github.kqfall1.java.blackjackEngine.model.enums.Rank;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.RuleViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class DoublingDownOnSplitHandsNotAllowedTest extends CustomDeckTest
{
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/DoublingDownOnSplitHandsNotAllowed.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.config.DoublingDownOnSplitHandsNotAllowed.log";

	@BeforeEach
	@Override
	public void init() {
		super.initCardsForSplittingWithoutHitting(Rank.TEN);
		super.initDependencies();
		super.ruleset.getConfig().setMaximumSplitCount(MAXIMUM_SPLIT_COUNT);
		super.initEngine(LOG_FILE_PATH, LOGGER_NAME);
		super.engine.getDealer().setCardSource(testDeck);
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	public void main()
	{
		Assertions.assertFalse(super.engine.getRuleset().getConfig().isDoublingDownOnSplitHandsAllowed());
		super.advanceToPlayerTurn(DOUBLE_DOWN_TEST_MAXIMUM_INITIAL_BET_AMOUNT);

		if (super.engine.getState() == BlackjackEngineState.PLAYER_TURN)
		{
			try
			{
				for (int count = 0; count < super.ruleset.getConfig().getMaximumSplitCount(); count++)
				{
					super.initSplitHands();
					super.engine.playerDoubleDown();
				}

				super.engine.playerDoubleDown();
			}
			catch (RuleViolationException e)
			{
				System.out.println(e.getMessage());
				super.engine.playerStand();
				super.engine.playerStand();
			}
		}

		super.advanceToShowdownAfterPlayerTurn();

		for (int count = 0; count < super.engine.getPlayer().getContexts().size() - 1; count++)
		{
			super.engine.showdown();
		}

		super.advanceToEndOfRoundAfterShowdown();
	}
}