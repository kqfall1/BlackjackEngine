package com.github.kqfall1.java.blackjackEngine.playerAction;

import com.github.kqfall1.java.blackjackEngine.engine.EngineTest;
import com.github.kqfall1.java.blackjackEngine.model.enums.BlackjackEngineState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class HitAndBustTest extends EngineTest
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
		advanceToPlayerTurn(engine.getPlayer().getChips().subtract(engine.getRuleset().getConfig().getMinimumBetAmount()));

		if (engine.getState() == BlackjackEngineState.PLAYER_TURN)
		{
			int previousCardCount;
			while (!ruleset.isHandBusted(engine.getActiveHandContext().getHand()))
			{
				previousCardCount = engine.getActiveHandContext().getHand().getCards().size();
				engine.playerHit();
				Assertions.assertTrue(
					engine.getActiveHandContext().getHand().getCards().size() == previousCardCount + 1
					&& engine.getActiveHandContext().isAltered()
				);
			}
		}

		advanceThroughShowdownsAfterPlayerTurn();
		advanceToEndOfRoundAfterShowdown();
	}
}