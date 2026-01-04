package com.github.kqfall1.java.blackjackEngine.model.cards;

import com.github.kqfall1.java.blackjackEngine.model.enums.Rank;
import com.github.kqfall1.java.blackjackEngine.model.enums.Suit;
import com.github.kqfall1.java.utils.StringUtils;
import java.util.Objects;

/**
 * Immutable objects that correspond to the cards found in a standard, 52-card deck.
 *
 * @author kqfall1
 * @since 13/12/2025
 */
public final class Card
{
	private final Rank rank;
	private final Suit suit;

	public Card(Rank rank, Suit suit)
	{
		assert rank != null : "rank == null";
		assert suit != null : "suit == null";
		this.rank = rank;
		this.suit = suit;
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

		final var otherCard = (Card) otherObject;
		return Objects.equals(getRank(), otherCard.getRank())
			&& Objects.equals(getSuit(), otherCard.getSuit());
	}

	public Rank getRank()
	{
		return rank;
	}

	public Suit getSuit()
	{
		return suit;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(getRank(), getSuit());
	}

	@Override
	public String toString()
	{
		return String.format(
			"%s[rank=%s,suit=%s]",
			getClass().getName(),
			getRank(),
			getSuit()
		);
	}

	public String toStringPretty()
	{
		return String.format(
			"%s of %ss",
			StringUtils.normalizeLower(getRank().toString()),
			StringUtils.normalizeLower(getSuit().toString())
		);
	}
}