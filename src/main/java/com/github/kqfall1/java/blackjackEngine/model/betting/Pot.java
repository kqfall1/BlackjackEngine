package com.github.kqfall1.java.blackjackEngine.model.betting;

import java.math.BigDecimal;

/**
 * A container for chips for {@code Dealer} and {@code Player} wagers.
 *
 * @author kqfall1
 * @since 13/12/2025
 */
public final class Pot
{
	private BigDecimal amount;

	public Pot()
	{
		amount = BigDecimal.ZERO;
	}

	public Pot(BigDecimal amount)
	{
		assert amount != null && amount.compareTo(BigDecimal.ZERO) > 0
			: "amount == null || amount.compareTo(BigDecimal.ZERO) <= 0";
		this.amount = amount;
	}

	public void addChips(BigDecimal amount)
	{
		assert amount != null &&  amount.compareTo(BigDecimal.ZERO) > 0 :
			"amount == null || amount.compareTo(BigDecimal.ZERO) <= 0";
		this.amount = this.amount.add(amount);
	}

	public BigDecimal getAmount()
	{
		return amount;
	}

	private void setAmount(BigDecimal amount)
	{
		assert amount != null &&  amount.compareTo(BigDecimal.ZERO) >= 0 :
			"amount == null || amount.compareTo(BigDecimal.ZERO) < 0";
		this.amount = amount;
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