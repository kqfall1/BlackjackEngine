package com.github.kqfall1.java.blackjackEngine.model.betting;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Immutable wagers placed by {@code Player} roles.
 *
 * @author kqfall1
 * @since 13/12/2025
 */
public final class Bet
{
	private final BigDecimal amount;

	public Bet(BigDecimal amount)
	{
		assert amount != null && amount.compareTo(BigDecimal.ZERO) > 0 :
			"amount == null || amount.compareTo(BigDecimal.ZERO) <= 0";
		this.amount = amount;
	}

	public BigDecimal getAmount()
	{
		return amount;
	}

	public BigDecimal getHalf()
	{
		return amount.divide(BigDecimal.TWO, RoundingMode.HALF_UP);
	}

	@Override
	public String toString()
	{
		return String.format(
			"%s[amount=%s]",
			getClass().getName(),
			getAmount()
		);
	}
}