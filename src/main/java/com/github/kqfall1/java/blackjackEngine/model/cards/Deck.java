package com.github.kqfall1.java.blackjackEngine.model.cards;

import com.github.kqfall1.java.blackjackEngine.model.exceptions.EmptyDeckException;
import java.util.*;

/**
 * Represents a standard deck of playing cards.
 *
 * @author kqfall1
 * @since 13/12/2025
 */
public class Deck
{
	final Queue<Card> cards;

	public Deck()
	{
		final List<Card> cardsList = new ArrayList<>();

		for (Rank rank : Rank.values())
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
		if (cards.isEmpty())
		{
			throw new EmptyDeckException(this);
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
		return Objects.equals(getCards(), otherDeck.getCards());
	}

	public List<Card> getCards()
	{
		return List.copyOf(cards);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(getCards());
	}

	@Override
	public String toString()
	{
		return String.format(
			"%s[cards=%s]",
			getClass().getName(),
			getCards()
		);
	}
}