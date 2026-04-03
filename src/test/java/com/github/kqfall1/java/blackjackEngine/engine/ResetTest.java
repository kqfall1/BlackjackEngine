package com.github.kqfall1.java.blackjackEngine.engine;

import com.github.kqfall1.java.blackjackEngine.model.enums.BlackjackEngineState;
import com.github.kqfall1.java.blackjackEngine.model.hands.Hand;
import com.github.kqfall1.java.blackjackEngine.model.enums.HandContextType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class ResetTest extends EngineTest
{
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/ResetTest.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.ResetTest.log";

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
		super.advanceThroughDealerTurn(super.engine.getPlayer().getChips());

		if (super.engine.getState() == BlackjackEngineState.DEALER_TURN)
		{
			super.engine.advanceAfterDealerTurn();
		}

		super.engine.showdown();
		super.engine.advanceAfterShowdown();
		super.engine.reset();
		Assertions.assertEquals(new Hand(), super.engine.getDealer().getHand());
		Assertions.assertEquals(HandContextType.MAIN.ordinal(), super.engine.getPlayer().getContexts().size());
		super.engine.advanceAfterReset();
	}
}