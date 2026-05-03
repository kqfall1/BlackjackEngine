package com.github.kqfall1.java.blackjackEngine.insurance;

import com.github.kqfall1.java.blackjackEngine.engine.CustomDeckTest;
import com.github.kqfall1.java.blackjackEngine.model.enums.BlackjackEngineState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class InsuranceBetDeclineAndSurrenderTest extends CustomDeckTest
{
	@BeforeEach
	@Override
	public void init()
	{
		initCardsForInsurance();
		initDependencies();
		ruleset.getConfig().setSurrenderingAllowed(true);
		initEngine();
		engine.getDealer().setCardSource(testDeck);
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	public void main()
	{
		advanceToPlayerTurn(INITIAL_PLAYER_CHIP_AMOUNT);

		if (engine.getState() == BlackjackEngineState.PLAYER_TURN)
		{
			engine.playerSurrender();
		}

		advanceThroughShowdownsAfterPlayerTurn();
		advanceToEndOfRoundAfterShowdown();
	}
}