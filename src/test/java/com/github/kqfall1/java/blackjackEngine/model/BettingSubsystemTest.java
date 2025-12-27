package com.github.kqfall1.java.blackjackEngine.model;

import com.github.kqfall1.java.blackjackEngine.model.betting.Bet;
import com.github.kqfall1.java.blackjackEngine.model.betting.PayoutRatio;
import com.github.kqfall1.java.blackjackEngine.model.betting.Pot;
import com.github.kqfall1.java.blackjackEngine.model.engine.StandardRuleConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Tests the functionalities of {@code Betting}, {@code PayoutRatio}, and {@code Pot}
 * objects.
 *
 * @author kqfall1
 * @since 20/12/2025
 */
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

	private void betTest(RepetitionInfo info)
	{
		assertEquals(bet1, bet2);
		Assertions.assertNotEquals(new Bet(BigDecimal.ONE), bet1);
		assertEquals(initialBetAmount, bet1.getAmount());
		assertEquals(
			initialBetAmount.divide(BigDecimal.TWO, MathContext.DECIMAL128),
			bet1.getHalf()
		);
		assertEquals(bet1.getAmount(), bet2.getAmount());
		assertEquals(bet1.getHalf(), bet2.getHalf());
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

	@RepeatedTest(TEST_ITERATIONS)
	void bettingSubsystemTest(RepetitionInfo info)
	{
		payoutRatioTest();
		betTest(info);
		potTest(info);
	}

	private void payoutRatioTest()
	{
		assertEquals(
			StandardRuleConfig.BLACKJACK,
			new PayoutRatio(BigDecimal.valueOf(3), BigDecimal.TWO)
		);
		Assertions.assertNotEquals(StandardRuleConfig.BLACKJACK, StandardRuleConfig.SURRENDER);
		assertEquals(BigDecimal.valueOf(3), StandardRuleConfig.BLACKJACK.getNumerator());
		assertEquals(BigDecimal.TWO, StandardRuleConfig.BLACKJACK.getDenominator());
		assertEquals(
			StandardRuleConfig.BLACKJACK.getNumerator().divide(
				StandardRuleConfig.BLACKJACK.getDenominator(),
				MathContext.DECIMAL128
			),
			StandardRuleConfig.BLACKJACK.getPayoutMultiplier()
		);

		assertEquals(BigDecimal.TWO, StandardRuleConfig.INSURANCE.getNumerator());
		assertEquals(BigDecimal.ONE, StandardRuleConfig.INSURANCE.getDenominator());
		assertEquals(
			StandardRuleConfig.INSURANCE.getNumerator().divide(
				StandardRuleConfig.INSURANCE.getDenominator(),
				MathContext.DECIMAL128
			),
			StandardRuleConfig.INSURANCE.getPayoutMultiplier()
		);

		assertEquals(BigDecimal.ONE, StandardRuleConfig.PUSH.getNumerator());
		assertEquals(BigDecimal.TWO, StandardRuleConfig.PUSH.getDenominator());
		assertEquals(
			StandardRuleConfig.PUSH.getNumerator().divide(
				StandardRuleConfig.PUSH.getDenominator(),
				MathContext.DECIMAL128
			),
			StandardRuleConfig.PUSH.getPayoutMultiplier()
		);

		assertEquals(BigDecimal.ONE, StandardRuleConfig.SURRENDER.getNumerator());
		assertEquals(BigDecimal.valueOf(4), StandardRuleConfig.SURRENDER.getDenominator());
		assertEquals(
			StandardRuleConfig.SURRENDER.getNumerator().divide(
				StandardRuleConfig.SURRENDER.getDenominator(),
				MathContext.DECIMAL128
			),
			StandardRuleConfig.SURRENDER.getPayoutMultiplier()
		);
	}

	private void potTest(RepetitionInfo info)
	{
		Assertions.assertNotEquals(pot1, pot2);
		assertEquals(BigDecimal.ZERO, pot1.getAmount());
		assertEquals(initialPotAmount, pot2.getAmount());
		pot1.addChips(bet1.getAmount());
		pot2.addChips(bet2.getAmount());
		assertEquals(bet1.getAmount(), pot1.getAmount());
		assertEquals(
			initialPotAmount.add(bet2.getAmount()).stripTrailingZeros(),
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