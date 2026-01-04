package com.github.kqfall1.java.blackjackEngine.model.cards;

import com.github.kqfall1.java.blackjackEngine.model.enums.Rank;
import com.github.kqfall1.java.blackjackEngine.model.enums.Suit;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.NoMoreCardsException;
import com.github.kqfall1.java.blackjackEngine.model.interfaces.Drawable;
import java.util.*;

/**
 * Represents a standard deck of playing {@code Card} objects.
 *
 * @author kqfall1
 * @since 13/12/2025
 */
public class Deck implements Drawable
{
	final Queue<Card> cards;
	final Rank[] includedRanks;

	public Deck()
	{
		this(Rank.values());
	}

	public Deck(Rank[] includedRanks)
	{
		assert includedRanks != null : "includedRanks == null";
		this.includedRanks = includedRanks;
		final List<Card> cardsList = new ArrayList<>();

		for (Rank rank : includedRanks)
		{
			for (Suit suit : Suit.values())
			{
				cardsList.add(new Card(rank, suit));
			}
		}

		Collections.shuffle(cardsList);
		cards = new ArrayDeque<>(cardsList);
	}

	public Card draw()
	{
		if (getCards().isEmpty())
		{
			throw new NoMoreCardsException(this);
		}

		return cards.poll();
	}

	@Override
	public boolean equals(Object otherObject)
	{
		if (this == otherObject)
		{
			return true;
		}
		else if (!(otherObject instanceof Deck))
		{
			return false;
		}

		final var otherDeck = (Deck) otherObject;
		return Objects.equals(getCards(), otherDeck.getCards())
			&& Arrays.equals(getIncludedRanks(), otherDeck.getIncludedRanks());
	}

	public List<Card> getCards()
	{
		return List.copyOf(cards);
	}

	Rank[] getIncludedRanks()
	{
		return includedRanks;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(getCards(), Arrays.hashCode(getIncludedRanks()));
	}

	@Override
	public String toString()
	{
		return String.format(
			"%s[cards=%s,includedRanks=%s]",
			getClass().getName(),
			getCards(),
			Arrays.toString(includedRanks)
		);
	}
}