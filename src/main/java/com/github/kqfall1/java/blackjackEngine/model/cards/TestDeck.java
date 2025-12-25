package com.github.kqfall1.java.blackjackEngine.model.cards;

import java.util.List;
import java.util.Queue;

/**
 * Exposes public mechanisms that make {@code BlackjackEngine} testing significantly
 * easier, especially regarding splitting operations.
 *
 * @author kqfall1
 * @since 23/12/2025
 */
public final class TestDeck extends Deck
{
	private Queue<Card> initialCards;

	@Override
	public Card draw()
	{
		if (!getInitialCards().isEmpty())
		{
			return initialCards.poll();
		}

		return cards.poll();
	}

	public List<Card> getInitialCards()
	{
		return initialCards == null
			? List.of()
			: List.copyOf(initialCards);
	}

	public void setInitialCards(Queue<Card> initialCards)
	{
		this.initialCards = initialCards;

		if (initialCards != null)
		{
			for (Card card : initialCards)
			{
				cards.remove(card);
			}
		}
	}

	@Override
	public String toString()
	{
		return String.format(
			"%s[initialCards=%s]",
			super.toString(),
			getInitialCards()
		);
	}
}