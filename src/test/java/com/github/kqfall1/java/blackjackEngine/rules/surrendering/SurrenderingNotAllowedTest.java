package com.github.kqfall1.java.blackjackEngine.rules.surrendering;

import com.github.kqfall1.java.blackjackEngine.engine.EngineTest;
import com.github.kqfall1.java.blackjackEngine.model.enums.BlackjackEngineState;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.RuleViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class SurrenderingNotAllowedTest extends EngineTest
{
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/SurrenderingNotAllowedTest.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.config.SurrenderingNotAllowedTest.log";

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
		Assertions.assertFalse(super.engine.getRuleset().getConfig().isSurrenderingAllowed());
		super.advanceToPlayerTurn(super.engine.getPlayer().getChips());

		if (super.engine.getState() == BlackjackEngineState.PLAYER_TURN)
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

		super.advanceToShowdownAfterPlayerTurn();
		super.advanceToEndOfRoundAfterShowdown();
	}
}