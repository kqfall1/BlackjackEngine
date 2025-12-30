package com.github.kqfall1.java.blackjackEngine.controllers.playerAction;

import com.github.kqfall1.java.blackjackEngine.controllers.EngineTest;
import com.github.kqfall1.java.blackjackEngine.model.engine.EngineState;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class HitAndBustTest extends EngineTest
{
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/HitAndBustTest.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.playerAction.HitAndBustTest.log";

	@BeforeEach
	@Override
	public void init() throws InsufficientChipsException, IOException
	{
		super.initDependencies();
		super.initEngine(LOG_FILE_PATH, LOGGER_NAME);
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	public void main() throws Exception
	{
		super.advanceToPlayerTurn(super.engine.getPlayer().getChips());

		if (super.engine.getState() == EngineState.PLAYER_TURN)
		{
			int previousCardCount;
			while (!super.engine.getActiveHandContext().getHand().isBusted())
			{
				previousCardCount = super.engine.getActiveHandContext().getHand().getCards().size();
				super.engine.playerHit();
				Assertions.assertTrue(
					super.engine.getActiveHandContext().getHand().getCards().size() == previousCardCount + 1
					&& super.engine.getActiveHandContext().isAltered()
				);
			}

			super.engine.advanceAfterPlayerTurn();
		}

		super.advanceToEndOfRound();
	}
}