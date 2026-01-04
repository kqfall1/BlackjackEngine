package com.github.kqfall1.java.blackjackEngine.model.cards;

import com.github.kqfall1.java.blackjackEngine.model.enums.Rank;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.NoMoreCardsException;
import com.github.kqfall1.java.utils.StringUtils;
import java.util.ArrayDeque;
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
		else if (!getCards().isEmpty())
		{
			return super.cards.poll();
		}

		throw new NoMoreCardsException(this);
	}

	public List<Card> getInitialCards()
	{
		return initialCards == null
			? List.of()
			: List.copyOf(initialCards);
	}

	public void removeCards(Card... cards)
	{
		for (Card card : cards)
		{
			super.cards.remove(card);
		}
	}

	public Card removeCardOfRank(Rank rank)
	{
		assert rank != null : "rank == null";

		for (Card card : getCards())
		{
			if (card.getRank() == rank)
			{
				if (initialCards != null)
				{
					initialCards.remove(card);
				}

				super.cards.remove(card);
				return card;
			}
		}

		throw new IllegalStateException(String.format(
			"No %s is present in test deck %s.",
			StringUtils.normalizeLower(rank.toString()),
			this
		));
	}

	public void setInitialCards(Queue<Card> initialCards)
	{
		assert initialCards != null && !initialCards.isEmpty()
			: "initialCards == null || initialCards.isEmpty()";

		final var includedRanksList = List.of(super.getIncludedRanks());

		for (Card card : initialCards)
		{
			assert card != null && includedRanksList.contains(card.getRank())
				: "card == null || !includedRanksList.contains(card.getRank())";
		}

		this.initialCards = new ArrayDeque<>(initialCards);

		for (Card card : initialCards)
		{
			removeCards(card);
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