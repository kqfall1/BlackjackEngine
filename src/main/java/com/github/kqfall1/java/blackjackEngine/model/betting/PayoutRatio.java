package com.github.kqfall1.java.blackjackEngine.model.betting;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Objects;

/**
 * Represent the ratios used by {@code BlackjackEngine} logic to calculate
 * {@code Player} winnings.
 *
 * @author kqfall1
 * @since 13/12/2025
 */
public final class PayoutRatio
{
	private final BigDecimal numerator;
	private final BigDecimal denominator;

	public PayoutRatio(BigDecimal numerator, BigDecimal denominator)
	{
		assert numerator != null && numerator.compareTo(BigDecimal.ZERO) >= 0 :
			"numerator == null || numerator.compareTo(BigDecimal.ZERO) < 0";
		assert denominator != null && denominator.compareTo(BigDecimal.ZERO) > 0 :
			"denominator == null || denominator.compareTo(BigDecimal.ZERO) <= 0";
		this.numerator = numerator.stripTrailingZeros();
		this.denominator = denominator.stripTrailingZeros();
	}

	@Override
	public boolean equals(Object otherObject)
	{
		if (this == otherObject)
		{
			return true;
		}
		else if (otherObject == null || getClass() != otherObject.getClass())
		{
			return false;
		}

		final var otherPayoutRatio = (PayoutRatio) otherObject;
		return Objects.equals(getNumerator(), otherPayoutRatio.getNumerator())
			&& Objects.equals(getDenominator(), otherPayoutRatio.getDenominator());
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
		return getNumerator()
			.divide(getDenominator(), MathContext.DECIMAL128)
			.stripTrailingZeros();
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(getNumerator(), getDenominator());
	}

	@Override
	public String toString()
	{
		return String.format(
			"%s[numerator=%s,denominator=%s,payoutMultiplier=%s]",
			getClass().getName(),
			getNumerator(),
			getDenominator(),
			getPayoutMultiplier()
		);
	}
}