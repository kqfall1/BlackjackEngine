package com.github.kqfall1.java.blackjackEngine.insurance;

import com.github.kqfall1.java.blackjackEngine.engine.CustomDeckTest;
import com.github.kqfall1.java.blackjackEngine.model.enums.BlackjackEngineState;
import java.math.BigDecimal;
import java.math.MathContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class InsuranceBetDeclineAndHitTest extends CustomDeckTest
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
		advanceToPlayerTurn(engine.getPlayer().getChips().divide(BigDecimal.TWO, MathContext.DECIMAL128));

		if (engine.getState() == BlackjackEngineState.PLAYER_TURN)
		{
			while (!ruleset.isHandBusted(engine.getActiveHandContext().getHand()))
			{
				engine.playerHit();
			}
		}

		advanceThroughShowdownsAfterPlayerTurn();
		advanceToEndOfRoundAfterShowdown();
	}
}