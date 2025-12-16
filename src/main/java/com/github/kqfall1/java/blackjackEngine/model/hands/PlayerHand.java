package com.github.kqfall1.java.blackjackEngine.model.hands;

import com.github.kqfall1.java.blackjackEngine.model.betting.Bet;
import java.util.Objects;

/**
 * A wrapper for a {@code Player} object's associated {@code Bet}, {@code Hand},
 * and {@code HandType} in a blackjack betting round.
 *
 * @author kqfall1
 * @since 14/12/2025
 */
public final class PlayerHand
{
	private Bet bet;
	private final Hand hand;
	private boolean hasSurrendered;
	private final HandType type;

	public PlayerHand(Bet bet, HandType type)
	{
		assert  type != null : "type == null";
		setBet(bet);
		this.hand = new Hand();
		this.type = type;
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

		final var otherPlayerHand = (PlayerHand) otherObject;
		return Objects.equals(getBet(), otherPlayerHand.getBet())
			&& Objects.equals(getHand(), otherPlayerHand.getHand())
			&& Objects.equals(getType(), otherPlayerHand.getType());
	}

	public Bet getBet()
	{
		return bet;
	}

	public Hand getHand()
	{
		return hand;
	}

	public boolean getHasSurrendered()
	{
		return hasSurrendered;
	}

	public HandType getType()
	{
		return type;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(getBet(), getHand(), getType());
	}

	public void setBet(Bet bet)
	{
		assert bet != null : "bet == null";
		this.bet = bet;
	}

	public void setHasSurrendered(boolean value)
	{
		hasSurrendered = value;
	}

	@Override
	public String toString()
	{
		return String.format(
			"%s[bet=%s,hand=%s,hasSurrendered=%s,type=%s]",
			getClass().getName(),
			getBet(),
			getHand(),
			getHasSurrendered(),
			getType()
		);
	}
}