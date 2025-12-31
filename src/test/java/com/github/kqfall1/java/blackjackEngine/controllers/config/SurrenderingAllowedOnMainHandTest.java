package com.github.kqfall1.java.blackjackEngine.controllers.config;

import com.github.kqfall1.java.blackjackEngine.controllers.CustomDeckTest;
import com.github.kqfall1.java.blackjackEngine.model.cards.Rank;
import com.github.kqfall1.java.blackjackEngine.model.engine.EngineState;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.RuleViolationException;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
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
		super.initCardsForSplitting(Rank.JACK);
		super.initDependencies();
		super.config.setMaximumSplitCount(MAXIMUM_SPLIT_COUNT);
		super.config.setSurrenderingAllowed(true);
		super.initEngine(LOG_FILE_PATH, LOGGER_NAME);
		super.engine.getDealer().setDeck(testDeck);
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	public void main() throws Exception
	{
		Assertions.assertTrue(super.engine.getConfig().isSurrenderingAllowed());
		Assertions.assertFalse(super.engine.getConfig().isSurrenderingOnSplitHandsAllowed());
		super.advanceToPlayerTurn(SPLIT_TEST_MAXIMUM_INITIAL_BET_AMOUNT);

		if (super.engine.getState() == EngineState.PLAYER_TURN)
		{
			super.initSplitHands();

			for (int count = 0;
				 count < super.engine.getConfig().getMaximumSplitCount();
				 count++)
			{
				try
				{
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