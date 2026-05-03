package com.github.kqfall1.java.blackjackEngine.insurance;

import com.github.kqfall1.java.blackjackEngine.engine.CustomDeckTest;
import com.github.kqfall1.java.blackjackEngine.model.enums.BlackjackEngineState;
import com.github.kqfall1.java.blackjackEngine.model.engine.BlackjackConstants;
import java.math.BigDecimal;
import java.math.MathContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class InsuranceBetAcceptTest extends CustomDeckTest
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
		placeRandomHandBet(INITIAL_PLAYER_CHIP_AMOUNT.divide(BigDecimal.TWO, MathContext.DECIMAL128));
		engine.deal();
		engine.advanceAfterDeal();

		final var chipsBeforeInsurance = engine.getPlayer().getChips();
		final var halfOfActiveBet = engine.getActiveHandContext().getBet().getHalf();
		var winnings = BigDecimal.ZERO;
		engine.acceptInsuranceBet();

		if (ruleset.isHandBlackjack(engine.getDealer().getHand()))
		{
			winnings = halfOfActiveBet.multiply(BlackjackConstants.INSURANCE_RATIO.getPayoutMultiplier());
			Assertions.assertTrue(
				nearlyEquals(
					chipsBeforeInsurance
						.subtract(halfOfActiveBet)
						.add(winnings)
						.stripTrailingZeros(),
					engine.getPlayer().getChips(),
					BlackjackConstants.DEFAULT_CHIP_SCALE
				)
			);
		}
		else
		{
			Assertions.assertTrue(
				nearlyEquals(
					chipsBeforeInsurance
						.subtract(halfOfActiveBet)
						.stripTrailingZeros(),
					engine.getPlayer().getChips(),
					BlackjackConstants.DEFAULT_CHIP_SCALE
				)
			);
		}

		engine.advanceAfterInsuranceBet(winnings);

		if (engine.getState() == BlackjackEngineState.PLAYER_TURN)
		{
			engine.playerStand();
		}

		advanceThroughShowdownsAfterPlayerTurn();
		advanceToEndOfRoundAfterShowdown();
	}
}