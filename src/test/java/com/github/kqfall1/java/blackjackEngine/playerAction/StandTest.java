package com.github.kqfall1.java.blackjackEngine.playerAction;

import com.github.kqfall1.java.blackjackEngine.engine.EngineTest;
import com.github.kqfall1.java.blackjackEngine.model.enums.EngineState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class StandTest extends EngineTest
{
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/StandTest.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.playerAction.StandTest.log";

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
		super.advanceToPlayerTurn(super.engine.getPlayer().getChips());

		if (super.engine.getState() == EngineState.PLAYER_TURN)
		{
			super.engine.playerStand();
			Assertions.assertTrue(super.engine.getActiveHandContext().isAltered());
		}

		super.advanceToEndOfRound();
	}
}