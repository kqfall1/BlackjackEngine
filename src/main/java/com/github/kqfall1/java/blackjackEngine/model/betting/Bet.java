package com.github.kqfall1.java.blackjackEngine.model.betting;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Immutable wagers placed by {@code Player} actors.
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
		this.amount = amount.stripTrailingZeros();
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

		final var otherBet = (Bet) otherObject;
		return Objects.equals(getAmount(), otherBet.getAmount());
	}

	public BigDecimal getAmount()
	{
		return amount;
	}

	public BigDecimal getHalf()
	{
		return getAmount().divide(BigDecimal.TWO, RoundingMode.HALF_UP);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(getAmount());
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