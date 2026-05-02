package com.github.kqfall1.java.blackjackEngine.model.cards;

import com.github.kqfall1.java.blackjackEngine.model.enums.Rank;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.NoMoreCardsException;
import com.github.kqfall1.java.utils.StringUtils;
import java.util.ArrayDeque;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * Exposes APIs that allows clients to select an initial sequence of {@code Card} objects, making
 * {@code BlackjackEngine} testing significantly easier.
 *
 * @author kqfall1
 * @since 23/12/2025
 */
public final class TestDeck extends Deck
{
	private Queue<Card> initialCards;

	public TestDeck()
	{
		initialCards = new ArrayDeque<>();
	}

	@Override
	public Card draw() throws NoMoreCardsException
	{
		try
		{
			try
			{
				return initialCards.remove();
			}
			catch (NoSuchElementException e)
			{
				return super.cards.remove();
			}
		}
		catch (NoSuchElementException e)
		{
			final var ex = new NoMoreCardsException(this);
			ex.initCause(e);
			throw ex;
		}
	}

	public List<Card> getInitialCards()
	{
		return initialCards == null ? List.of() : List.copyOf(initialCards);
	}

	public Card removeCardOfRank(Rank rank)
	{
		assert rank != null : "rank == null";

		for (final var card : getCards())
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

		throw new IllegalStateException(String.format("No %s is present in test deck %s.", StringUtils.normalizeLower(rank.toString()), this));
	}

	public void setInitialCards(Queue<Card> initialCards)
	{
		assert initialCards != null && !initialCards.isEmpty() : "initialCards == null || initialCards.isEmpty()";
		final var includedRanksList = List.of(super.getIncludedRanks());

		for (final var card : initialCards)
		{
			assert card != null && includedRanksList.contains(card.getRank()) : "card == null || !includedRanksList.contains(card.getRank())";
		}

		this.initialCards = new ArrayDeque<>(initialCards);

		for (final var card : initialCards)
		{
			super.cards.remove(card);
		}
	}

	@Override
	public String toString()
	{
		return String.format("%s[initialCards=%s]", super.toString(), getInitialCards());
	}
}