package com.github.kqfall1.java.blackjackEngine.controllers.playerAction.splitting;

import com.github.kqfall1.java.blackjackEngine.controllers.CustomDeckTest;
import com.github.kqfall1.java.blackjackEngine.model.cards.Rank;
import com.github.kqfall1.java.blackjackEngine.model.engine.EngineState;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class SplitAndDoubleDownTest extends CustomDeckTest
{
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/SplitAndDoubleDownTest.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.playerAction.splitting.SplitAndDoubleDownTest.log";

	@BeforeEach
	@Override
	public void init() throws InsufficientChipsException, IOException
	{
		super.initCardsForSplitting(Rank.KING);
		super.initDependencies();
		super.config.setDoublingDownOnSplitHandsAllowed(true);
		super.config.setMaximumSplitCount(MAXIMUM_SPLIT_COUNT);
		super.initEngine(LOG_FILE_PATH, LOGGER_NAME);
		super.engine.getDealer().setCardSource(testDeck);
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	public void main() throws Exception
	{
		super.advanceToPlayerTurn(DOUBLE_DOWN_TEST_MAXIMUM_INITIAL_BET_AMOUNT);

		if (super.engine.getState() == EngineState.PLAYER_TURN)
		{
			super.initSplitHands();

			while (!super.engine.getActiveHandContext().isAltered())
			{
				super.engine.playerDoubleDown();
			}

			super.engine.advanceAfterPlayerTurn();
		}

		super.advanceToEndOfRound();
	}
}