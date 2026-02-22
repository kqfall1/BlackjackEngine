package com.github.kqfall1.java.blackjackEngine.engine;

import com.github.kqfall1.java.blackjackEngine.model.enums.EngineState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class IntegrationTest extends EngineTest
{
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/IntegrationTest.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.IntegrationTest.log";

	@BeforeEach
	@Override
	public void init() {
		super.initDependencies();
		super.initEngine(LOG_FILE_PATH, LOGGER_NAME);
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	public void main()
	{
		while (super.engine.getState() != EngineState.END)
		{
			super.advanceToPlayerTurn(super.engine.getPlayer().getChips());

			if (super.engine.getState() == EngineState.PLAYER_TURN)
			{
				super.engine.playerHit();

				if (super.engine.getState() == EngineState.PLAYER_TURN)
				{
					super.engine.playerStand();
				}
			}

			super.advanceToEndOfRound();
		}
	}
}