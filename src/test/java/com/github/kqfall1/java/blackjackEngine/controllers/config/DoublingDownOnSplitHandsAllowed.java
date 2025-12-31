package com.github.kqfall1.java.blackjackEngine.controllers.config;

import com.github.kqfall1.java.blackjackEngine.controllers.CustomDeckTest;
import com.github.kqfall1.java.blackjackEngine.model.cards.Rank;
import com.github.kqfall1.java.blackjackEngine.model.engine.EngineState;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class DoublingDownOnSplitHandsAllowed extends CustomDeckTest
{
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/DoublingDownOnSplitHandsAllowedTest.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.config.DoublingDownOnSplitHandsAllowedTest.log";

	@BeforeEach
	@Override
	public void init() throws InsufficientChipsException, IOException
	{
		super.initCardsForSplitting(Rank.TEN);
		super.initDependencies();
		super.config.setMaximumSplitCount(MAXIMUM_SPLIT_COUNT);
		super.config.setDoublingDownOnSplitHandsAllowed(true);
		super.initEngine(LOG_FILE_PATH, LOGGER_NAME);
		super.engine.getDealer().setDeck(testDeck);
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	public void main() throws Exception
	{
		Assertions.assertTrue(super.engine.getConfig().isDoublingDownOnSplitHandsAllowed());
		super.advanceToPlayerTurn(DOUBLE_DOWN_TEST_MAXIMUM_INITIAL_BET_AMOUNT);

		if (super.engine.getState() == EngineState.PLAYER_TURN)
		{
			super.initSplitHands();

			for (int count = 0;
				 count < super.engine.getConfig().getMaximumSplitCount();
			 	count++)
			{
				super.engine.playerDoubleDown();
			}

			super.engine.advanceAfterPlayerTurn();
			super.advanceToEndOfRound();
		}
	}
}