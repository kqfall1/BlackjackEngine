package com.github.kqfall1.java.blackjackEngine.rules.surrendering;

import com.github.kqfall1.java.blackjackEngine.engine.CustomDeckTest;
import com.github.kqfall1.java.blackjackEngine.model.enums.Rank;
import com.github.kqfall1.java.blackjackEngine.model.enums.EngineState;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.RuleViolationException;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class SurrenderingAllowedOnMainHandTest extends CustomDeckTest
{
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/SurrenderingAllowedOnMainHandTest.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.config.SurrenderingAllowedOnMainHandTest.log";

	@BeforeEach
	@Override
	public void init() throws InsufficientChipsException, IOException
	{
		super.initCardsForSplittingWithoutHitting(Rank.JACK);
		super.initDependencies();
		super.ruleset.getConfig().setMaximumSplitCount(MAXIMUM_SPLIT_COUNT);
		super.ruleset.getConfig().setSurrenderingAllowed(true);
		super.initEngine(LOG_FILE_PATH, LOGGER_NAME);
		super.engine.getDealer().setCardSource(testDeck);
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	public void main() throws Exception
	{
//		Assertions.assertTrue(super.config.isSurrenderingAllowed());
//		Assertions.assertFalse(super.engine.getConfig().isSurrenderingOnSplitHandsAllowed());
		super.advanceToPlayerTurn(SPLIT_TEST_MAXIMUM_INITIAL_BET_AMOUNT);

		if (super.engine.getState() == EngineState.PLAYER_TURN)
		{
			for (int count = 0;
				 count < super.ruleset.getConfig().getMaximumSplitCount();
				 count++)
			{
				try
				{
					super.initSplitHands();
					super.engine.playerSurrender();
				}
				catch (RuleViolationException e)
				{
					System.out.println(e.getMessage());
					super.engine.playerStand();
				}
			}

			super.engine.advanceAfterPlayerTurn();
			super.advanceToEndOfRound();
		}
	}
}