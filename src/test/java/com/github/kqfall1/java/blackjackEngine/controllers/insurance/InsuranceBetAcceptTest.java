package com.github.kqfall1.java.blackjackEngine.controllers.insurance;

import com.github.kqfall1.java.blackjackEngine.controllers.CustomDeckTest;
import com.github.kqfall1.java.blackjackEngine.model.enums.EngineState;
import com.github.kqfall1.java.blackjackEngine.model.engine.BlackjackConstants;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class InsuranceBetAcceptTest extends CustomDeckTest
{
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/InsuranceBetAcceptTest.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.insurance.InsuranceBetAcceptTest.log";

	@BeforeEach
	@Override
	public void init() throws InsufficientChipsException, IOException
	{
		super.initCardsForInsurance();
		super.initDependencies();
		super.initEngine(LOG_FILE_PATH, LOGGER_NAME);
		super.engine.getDealer().setCardSource(testDeck);
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	public void main() throws Exception
	{
		super.placeRandomHandBet(INITIAL_PLAYER_CHIP_AMOUNT.divide(
			BigDecimal.TWO,
			MathContext.DECIMAL128
		));
		super.engine.deal();
		super.engine.advanceAfterDeal();

		final var CHIPS_BEFORE_INSURANCE = super.engine.getPlayer().getChips();
		final var HALF_OF_ACTIVE_BET = super.engine.getActiveHandContext().getBet().getHalf();
		super.engine.acceptInsuranceBet();

		var winnings = BigDecimal.ZERO;
		if (super.engine.getDealer().getHand().isBlackjack())
		{
			winnings = HALF_OF_ACTIVE_BET.multiply(
				BlackjackConstants.INSURANCE_RATIO.getPayoutMultiplier()
			);
			Assertions.assertTrue(
				nearlyEquals(
					CHIPS_BEFORE_INSURANCE
						.subtract(HALF_OF_ACTIVE_BET)
						.add(winnings)
						.stripTrailingZeros(),
					super.engine.getPlayer().getChips(),
					BlackjackConstants.DEFAULT_CHIP_SCALE
				)
			);
		}
		else
		{
			Assertions.assertTrue(
				nearlyEquals(
					CHIPS_BEFORE_INSURANCE
						.subtract(HALF_OF_ACTIVE_BET)
						.stripTrailingZeros(),
					super.engine.getPlayer().getChips(),
					BlackjackConstants.DEFAULT_CHIP_SCALE
				)
			);
		}

		super.engine.advanceAfterInsuranceBet(winnings);

		if (super.engine.getState() == EngineState.PLAYER_TURN)
		{
			super.engine.playerStand();
			super.engine.advanceAfterPlayerTurn();
		}

		super.advanceToEndOfRound();
	}
}