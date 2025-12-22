package com.github.kqfall1.java.blackjackEngine.model.hands;

import com.github.kqfall1.java.blackjackEngine.model.betting.Bet;
import com.github.kqfall1.java.blackjackEngine.model.betting.Pot;
import java.util.Objects;

/**
 * A wrapper for a {@code Player} object's associated {@code Bet}, {@code Hand},
 * {@code HandType}, and {@code Pot} in a blackjack betting round.
 *
 * @author kqfall1
 * @since 14/12/2025
 */
public final class HandContext
{
	/**
 	 * Indicates whether the {@code Player} that encapsulates this {@code HandContext} has
	 * taken any action on this {@code HandContext}, such as adding a {@code Card} to this
	 * {@code HandContext} object's {@code Hand}, surrendering, standing, or splitting.
 	 */
	private boolean altered;
	private Bet bet;
	private final Hand hand;
	private boolean hasSurrendered;
	private final Pot pot;
	private final HandContextType type;

	public HandContext(Bet bet, HandContextType type)
	{
		assert  type != null : "type == null";
		setBet(bet);
		this.hand = new Hand();
		this.pot = new Pot();
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

		final var otherContext = (HandContext) otherObject;
		return Objects.equals(getBet(), otherContext.getBet())
			&& Objects.equals(getHand(), otherContext.getHand())
			&& Objects.equals(getType(), otherContext.getType());
	}

	public Bet getBet()
	{
		return bet;
	}

	public Hand getHand()
	{
		return hand;
	}

	public Pot getPot()
	{
		return pot;
	}

	public HandContextType getType()
	{
		return type;
	}

	public boolean hasSurrendered()
	{
		return hasSurrendered;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(getBet(), getHand(), getType());
	}

	public boolean isAltered()
	{
		return altered;
	}

	/**
 	 * All {@code BlackjackEngine} processes involving {@code Player} actions need to call
	 * this method whenever this {@code HandContext} is altered.
 	 */
	public void markAsAltered()
	{
		altered = true;
	}

	public void setBet(Bet bet)
	{
		assert bet != null : "bet == null";
		this.bet = bet;
	}

	public void setHasSurrendered()
	{
		hasSurrendered = true;
		markAsAltered();
	}

	@Override
	public String toString()
	{
		return String.format(
			"%s[altered=%s,bet=%s,hand=%s,hasSurrendered=%s,pot=%s,type=%s]",
			getClass().getName(),
			isAltered(),
			getBet(),
			getHand(),
			hasSurrendered(),
			getPot(),
			getType()
		);
	}
}