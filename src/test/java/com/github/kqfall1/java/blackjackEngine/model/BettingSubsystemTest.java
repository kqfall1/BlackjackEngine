package com.github.kqfall1.java.blackjackEngine.model;

import com.github.kqfall1.java.blackjackEngine.controllers.EngineTest;
import com.github.kqfall1.java.blackjackEngine.model.betting.Bet;
import com.github.kqfall1.java.blackjackEngine.model.betting.PayoutRatio;
import com.github.kqfall1.java.blackjackEngine.model.betting.Pot;
import com.github.kqfall1.java.blackjackEngine.model.engine.BlackjackConstants;
import java.math.BigDecimal;
import java.math.MathContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;

final class BettingSubsystemTest
{
	private Bet bet1;
	private Bet bet2;
	private BigDecimal initialBetAmount;
	private BigDecimal initialPotAmount;
	private static final int MAXIMUM_BET_AMOUNT = 1000;
	private static final int MAXIMUM_POT_AMOUNT = 10000;
	private Pot pot1;
	private Pot pot2;
	private static final int TEST_ITERATIONS = 5000;

	private void betTest(RepetitionInfo info)
	{
		assertEquals(bet1, bet2);
		Assertions.assertNotEquals(new Bet(BigDecimal.ONE), bet1);
		assertTrue(
			EngineTest.nearlyEquals(
				initialBetAmount,
				bet1.getAmount(),
				BlackjackConstants.DEFAULT_CHIP_SCALE
			)
		);
		assertTrue(
			EngineTest.nearlyEquals(
				initialBetAmount.divide(BigDecimal.TWO, MathContext.DECIMAL128),
				bet1.getHalf(),
				BlackjackConstants.DEFAULT_CHIP_SCALE
			)
		);
		assertTrue(
			EngineTest.nearlyEquals(
				bet1.getAmount(),
				bet2.getAmount(),
				BlackjackConstants.DEFAULT_CHIP_SCALE
			)
		);
		assertTrue(
			EngineTest.nearlyEquals(
				bet1.getHalf(),
				bet2.getHalf(),
				BlackjackConstants.DEFAULT_CHIP_SCALE
			)
		);
		assertEquals(bet1.toString(), bet2.toString());

		try
		{
			if (info.getCurrentRepetition() % 3 == 0)
			{
				new Bet(BigDecimal.ZERO);
			}
			else if (info.getCurrentRepetition() % 3 == 1)
			{
				new Bet(null);
			}
			else
			{
				new Bet(BigDecimal.valueOf(-(Math.random() * MAXIMUM_BET_AMOUNT)));
			}
		}
		catch (AssertionError e)
		{
			System.out.printf("%s\n", e.getMessage());
		}
	}

	@BeforeEach
	void init()
	{
		initialBetAmount = new BigDecimal(Math.random() * MAXIMUM_BET_AMOUNT);
		initialPotAmount = new BigDecimal(Math.random() * MAXIMUM_POT_AMOUNT);
		bet1 = new Bet(initialBetAmount);
		bet2 = new Bet(initialBetAmount);
		pot1 = new Pot();
		pot2 = new Pot(initialPotAmount);
	}

	@RepeatedTest(TEST_ITERATIONS)
	void main(RepetitionInfo info)
	{
		payoutRatioTest();
		betTest(info);
		potTest(info);
	}

	private void payoutRatioTest()
	{
		assertEquals(
			BlackjackConstants.BLACKJACK_RATIO,
			new PayoutRatio(BigDecimal.valueOf(3), BigDecimal.TWO)
		);
		Assertions.assertNotEquals(BlackjackConstants.BLACKJACK_RATIO, BlackjackConstants.SURRENDER_RATIO);
		assertEquals(BigDecimal.valueOf(3), BlackjackConstants.BLACKJACK_RATIO.getNumerator());
		assertEquals(BigDecimal.TWO, BlackjackConstants.BLACKJACK_RATIO.getDenominator());
		assertEquals(
			BlackjackConstants.BLACKJACK_RATIO.getNumerator().divide(
				BlackjackConstants.BLACKJACK_RATIO.getDenominator(),
				MathContext.DECIMAL128
			),
			BlackjackConstants.BLACKJACK_RATIO.getPayoutMultiplier()
		);

		assertEquals(BigDecimal.TWO,	BlackjackConstants.INSURANCE_RATIO.getNumerator());
		assertEquals(BigDecimal.ONE, BlackjackConstants.INSURANCE_RATIO.getDenominator());
		assertEquals(
			BlackjackConstants.INSURANCE_RATIO.getNumerator().divide(
				BlackjackConstants.INSURANCE_RATIO.getDenominator(),
				MathContext.DECIMAL128
			),
			BlackjackConstants.INSURANCE_RATIO.getPayoutMultiplier()
		);

		assertEquals(BigDecimal.ONE, BlackjackConstants.PUSH_RATIO.getNumerator());
		assertEquals(BigDecimal.TWO, BlackjackConstants.PUSH_RATIO.getDenominator());
		assertEquals(
			BlackjackConstants.PUSH_RATIO.getNumerator().divide(
				BlackjackConstants.PUSH_RATIO.getDenominator(),
				MathContext.DECIMAL128
			),
			BlackjackConstants.PUSH_RATIO.getPayoutMultiplier()
		);

		assertEquals(BigDecimal.ONE, BlackjackConstants.SURRENDER_RATIO.getNumerator());
		assertEquals(BigDecimal.valueOf(4), BlackjackConstants.SURRENDER_RATIO.getDenominator());
		assertEquals(
			BlackjackConstants.SURRENDER_RATIO.getNumerator().divide(
				BlackjackConstants.SURRENDER_RATIO.getDenominator(),
				MathContext.DECIMAL128
			),
			BlackjackConstants.SURRENDER_RATIO.getPayoutMultiplier()
		);
	}

	private void potTest(RepetitionInfo info)
	{
		Assertions.assertNotEquals(pot1, pot2);
		assertEquals(BigDecimal.ZERO, pot1.getAmount());
		assertTrue(
			EngineTest.nearlyEquals(
				initialPotAmount,
				pot2.getAmount(),
				BlackjackConstants.DEFAULT_CHIP_SCALE
			)
		);
		pot1.addChips(bet1.getAmount());
		pot2.addChips(bet2.getAmount());
		assertEquals(bet1.getAmount().stripTrailingZeros(), pot1.getAmount());
		assertEquals(
			initialPotAmount.add(bet2.getAmount()),
			pot2.getAmount()
		);
		pot1.scoop();
		pot2.scoop();
		assertEquals(pot1.getAmount(), pot2.getAmount());
		assertEquals(pot1.toString(), pot2.toString());

		try
		{
			if (info.getCurrentRepetition() % 4 == 0)
			{
				new Pot(BigDecimal.ZERO);
			}
			else if (info.getCurrentRepetition() % 4 == 1)
			{
				new Pot(null);
			}
			else if (info.getCurrentRepetition() % 4 == 2)
			{
				pot1.addChips(BigDecimal.ZERO);
			}
			else
			{
				pot1.addChips(null);
			}
		}
		catch (AssertionError e)
		{
			System.out.printf("%s\n", e.getMessage());
		}
	}
}