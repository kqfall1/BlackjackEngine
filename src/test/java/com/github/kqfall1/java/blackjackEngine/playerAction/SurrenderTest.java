package com.github.kqfall1.java.blackjackEngine.playerAction;

import com.github.kqfall1.java.blackjackEngine.engine.EngineTest;
import com.github.kqfall1.java.blackjackEngine.model.enums.BlackjackEngineState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class SurrenderTest extends EngineTest
{
	@BeforeEach
	@Override
	public void init()
	{
		initDependencies();
		ruleset.getConfig().setSurrenderingAllowed(true);
		initEngine();
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	public void main()
	{
		advanceToPlayerTurn(engine.getPlayer().getChips());

		if (engine.getState() == BlackjackEngineState.PLAYER_TURN)
		{
			Assertions.assertFalse(engine.getActiveHandContext().isAltered());
			engine.playerSurrender();
			Assertions.assertTrue(engine.getActiveHandContext().isSurrendered() && engine.getActiveHandContext().isAltered());
		}

		advanceThroughShowdownsAfterPlayerTurn();
		advanceToEndOfRoundAfterShowdown();
	}
}