package com.github.kqfall1.java.blackjackEngine.model.betting;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * A container for chips for {@code Player} wagers and {@code Dealer} contributions.
 *
 * @author kqfall1
 * @since 13/12/2025
 */
public final class Pot
{
	private BigDecimal amount;

	public Pot()
	{
		setAmount(BigDecimal.ZERO);
	}

	public Pot(BigDecimal amount)
	{
		setAmount(amount);
	}

	public void addChips(BigDecimal amount)
	{
		assert amount != null &&  amount.compareTo(BigDecimal.ZERO) > 0 :
			"amount == null || amount.compareTo(BigDecimal.ZERO) <= 0";
		setAmount(getAmount().add(amount));
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

		final var otherPot = (Pot) otherObject;
		return Objects.equals(getAmount(), otherPot.getAmount());
	}

	public BigDecimal getAmount()
	{
		return amount;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(getAmount());
	}

	private void setAmount(BigDecimal amount)
	{
		assert amount != null &&  amount.compareTo(BigDecimal.ZERO) >= 0 :
			"amount == null || amount.compareTo(BigDecimal.ZERO) < 0";
		this.amount = amount.stripTrailingZeros();
	}

	public BigDecimal scoop()
	{
		final var winnings = getAmount();
		setAmount(BigDecimal.ZERO);
		return winnings;
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