package com.github.kqfall1.java.blackjackEngine.model.betting;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Represent the ratios used by {@code PayoutEngine} logic to calculate winnings.
 *
 * @author kqfall1
 * @since 13/12/2025
 */
public final class PayoutRatio
{
	private final BigDecimal denominator;
	private final BigDecimal numerator;

	public PayoutRatio(BigDecimal numerator, BigDecimal denominator)
	{
		assert numerator != null && numerator.compareTo(BigDecimal.ZERO) >= 0 :
			"numerator == null || numerator.compareTo(BigDecimal.ZERO) < 0";
		assert denominator != null && denominator.compareTo(BigDecimal.ZERO) > 0 :
			"denominator == null || denominator.compareTo(BigDecimal.ZERO) <= 0";
		this.numerator = numerator;
		this.denominator = denominator;
	}

	public BigDecimal getDenominator()
	{
		return denominator;
	}

	public BigDecimal getNumerator()
	{
		return numerator;
	}

	public BigDecimal getPayoutMultiplier()
	{
		return numerator.divide(denominator, MathContext.DECIMAL128);
	}

	@Override
	public String toString()
	{
		return String.format(
			"%s[denominator=%s,numerator=%s,payoutMultiplier=%s]",
			getClass().getName(),
			getDenominator(),
			getNumerator(),
			getPayoutMultiplier()
		);
	}
}