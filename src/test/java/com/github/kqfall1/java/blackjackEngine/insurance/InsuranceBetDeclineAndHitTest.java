package com.github.kqfall1.java.blackjackEngine.insurance;

import com.github.kqfall1.java.blackjackEngine.engine.CustomDeckTest;
import com.github.kqfall1.java.blackjackEngine.model.enums.EngineState;

import java.math.BigDecimal;
import java.math.MathContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class InsuranceBetDeclineAndHitTest extends CustomDeckTest
{
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/InsuranceBetDeclineAndHitTest.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.insurance.InsuranceBetDeclineAndHitTest.log";

	@BeforeEach
	@Override
	public void init() {
		super.initCardsForInsurance();
		super.initDependencies();
		super.initEngine(LOG_FILE_PATH, LOGGER_NAME);
		super.engine.getDealer().setCardSource(testDeck);
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	public void main()
	{
		super.advanceToPlayerTurn(
			super.engine.getPlayer().getChips().divide(
				BigDecimal.TWO,
				MathContext.DECIMAL128
			)
		);

		if (super.engine.getState() == EngineState.PLAYER_TURN)
		{
			while (!super.ruleset.isHandBusted(super.engine.getActiveHandContext().getHand()))
			{
				super.engine.playerHit();
			}

			super.engine.advanceAfterPlayerTurn();
		}

		super.advanceToEndOfRound();
	}
}