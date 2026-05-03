package com.github.kqfall1.java.blackjackEngine.rules.surrendering;

import com.github.kqfall1.java.blackjackEngine.engine.EngineTest;
import com.github.kqfall1.java.blackjackEngine.model.enums.BlackjackEngineState;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.RuleViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class SurrenderingNotAllowedTest extends EngineTest
{
	@BeforeEach
	@Override
	public void init()
	{
		initDependencies();
		initEngine();
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	public void main()
	{
		Assertions.assertFalse(engine.getRuleset().getConfig().isSurrenderingAllowed());
		advanceToPlayerTurn(engine.getPlayer().getChips());

		if (engine.getState() == BlackjackEngineState.PLAYER_TURN)
		{
			try
			{
				engine.playerSurrender();
			}
			catch (RuleViolationException e)
			{
				System.out.println(e.getMessage());
				engine.playerStand();
			}
		}

		advanceThroughShowdownsAfterPlayerTurn();
		advanceToEndOfRoundAfterShowdown();
	}
}