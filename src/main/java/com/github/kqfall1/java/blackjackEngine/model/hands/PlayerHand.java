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
	private final Bet bet;
	private final Hand hand;
	private final HandType type;

	public PlayerHand(Bet bet, HandType type)
	{
		assert bet != null : "bet == null";
		assert  type != null : "type == null";
		this.bet = bet;
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

	public HandType getType()
	{
		return type;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(getBet(), getHand(), getType());
	}

	@Override
	public String toString()
	{
		return String.format(
			"%s[bet=%s,hand=%s,type=%s]",
			getClass().getName(),
			getBet(),
			getHand(),
			getType()
		);
	}
}