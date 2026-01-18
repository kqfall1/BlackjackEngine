package com.github.kqfall1.java.blackjackEngine.engine;

import com.github.kqfall1.java.blackjackEngine.model.enums.EngineState;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import com.github.kqfall1.java.blackjackEngine.model.hands.Hand;
import com.github.kqfall1.java.blackjackEngine.model.enums.HandContextType;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class ResetTest extends EngineTest
{
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/ResetTest.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.ResetTest.log";

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
		super.advanceToDealerTurn(super.engine.getPlayer().getChips());

		if (super.engine.getState() == EngineState.DEALER_TURN)
		{
			super.engine.advanceAfterDealerTurn();
		}

		super.engine.advanceAfterShowdown();

		Assertions.assertEquals(
			new Hand(),
			super.engine.getDealer().getHand()
		);
		Assertions.assertEquals(
			HandContextType.MAIN.ordinal(),
			super.engine.getPlayer().getContexts().size()
		);

		super.engine.advanceAfterReset();
	}
}