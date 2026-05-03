package com.github.kqfall1.java.blackjackEngine.insurance;

import com.github.kqfall1.java.blackjackEngine.engine.CustomDeckTest;
import com.github.kqfall1.java.blackjackEngine.model.enums.BlackjackEngineState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class InsuranceBetDeclineAndStandTest extends CustomDeckTest
{
	@BeforeEach
	@Override
	public void init()
	{
		initCardsForInsurance();
		initDependencies();
		initEngine();
		engine.getDealer().setCardSource(testDeck);
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	public void main()
	{
		advanceThroughDealerTurn(engine.getPlayer().getChips());

		if (engine.getState() == BlackjackEngineState.DEALER_TURN)
		{
			engine.advanceAfterDealerTurn();
		}

		engine.showdown();
		advanceToEndOfRoundAfterShowdown();
	}
}