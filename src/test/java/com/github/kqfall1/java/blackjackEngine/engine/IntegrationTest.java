package com.github.kqfall1.java.blackjackEngine.engine;

import com.github.kqfall1.java.blackjackEngine.model.enums.BlackjackEngineState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class IntegrationTest extends EngineTest
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
		while (engine.getState() != BlackjackEngineState.END)
		{
			if (engine.getPlayer().getChips().compareTo(engine.getRuleset().getConfig().getMinimumBetAmount().add(CHIP_AMOUNT_BUFFER)) >= 0)
			{
				advanceToPlayerTurn(engine.getPlayer().getChips().subtract(engine.getRuleset().getConfig().getMinimumBetAmount()));
			}
			else
			{
				engine.placeBet(engine.getPlayer().getChips());
				engine.deal();
				engine.advanceAfterDeal();
				declinePossibleInsuranceBet();
			}

			if (engine.getState() == BlackjackEngineState.PLAYER_TURN)
			{
				engine.playerHit();

				if (engine.getState() == BlackjackEngineState.PLAYER_TURN)
				{
					engine.playerStand();
				}
			}

			advanceThroughShowdownsAfterPlayerTurn();
			advanceToEndOfRoundAfterShowdown();
		}
	}
}