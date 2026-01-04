package com.github.kqfall1.java.blackjackEngine.controllers.config;

import com.github.kqfall1.java.blackjackEngine.controllers.EngineTest;
import com.github.kqfall1.java.blackjackEngine.model.enums.EngineState;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.RuleViolationException;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class SurrenderingNotAllowedTest extends EngineTest
{
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/SurrenderingNotAllowedTest.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.config.SurrenderingNotAllowedTest.log";

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
//		Assertions.assertFalse(super.engine.getConfig().isSurrenderingAllowed());
		super.advanceToPlayerTurn(super.engine.getPlayer().getChips());

		if (super.engine.getState() == EngineState.PLAYER_TURN)
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

			super.engine.advanceAfterPlayerTurn();
			super.advanceToEndOfRound();
		}
	}
}