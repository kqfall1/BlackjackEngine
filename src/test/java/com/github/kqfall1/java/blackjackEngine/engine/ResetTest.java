package com.github.kqfall1.java.blackjackEngine.engine;

import com.github.kqfall1.java.blackjackEngine.model.enums.BlackjackEngineState;
import com.github.kqfall1.java.blackjackEngine.model.hands.Hand;
import com.github.kqfall1.java.blackjackEngine.model.enums.HandContextType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class ResetTest extends EngineTest
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
		advanceThroughDealerTurn(engine.getPlayer().getChips().subtract(engine.getRuleset().getConfig().getMinimumBetAmount()));

		if (engine.getState() == BlackjackEngineState.DEALER_TURN)
		{
			engine.advanceAfterDealerTurn();
		}

		engine.showdown();
		engine.advanceAfterShowdown();
		engine.reset();
		Assertions.assertEquals(new Hand(), engine.getDealer().getHand());
		Assertions.assertEquals(HandContextType.MAIN.ordinal(), engine.getPlayer().getContexts().size());
		engine.advanceAfterReset();
	}
}