package com.github.kqfall1.java.blackjackEngine.playerAction;

import com.github.kqfall1.java.blackjackEngine.engine.EngineTest;
import com.github.kqfall1.java.blackjackEngine.model.enums.EngineState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class SurrenderTest extends EngineTest
{
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/SurrenderTest.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.playerAction.SurrenderTest.log";

	@BeforeEach
	@Override
	public void init() {
		super.initDependencies();
		super.ruleset.getConfig().setSurrenderingAllowed(true);
		super.initEngine(LOG_FILE_PATH, LOGGER_NAME);
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	public void main()
	{
		super.advanceToPlayerTurn(super.engine.getPlayer().getChips());

		if (super.engine.getState() == EngineState.PLAYER_TURN)
		{
			Assertions.assertFalse(engine.getActiveHandContext().isAltered());
			super.engine.playerSurrender();
			Assertions.assertTrue(
				super.engine.getActiveHandContext().hasSurrendered()
				&& super.engine.getActiveHandContext().isAltered()
			);
			super.engine.advanceAfterPlayerTurn();
		}

		super.advanceToEndOfRound();
	}
}