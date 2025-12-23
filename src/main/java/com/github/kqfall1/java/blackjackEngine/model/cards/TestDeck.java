package com.github.kqfall1.java.blackjackEngine.model.cards;

import com.github.kqfall1.java.blackjackEngine.model.hands.Hand;
import com.github.kqfall1.java.utils.StringUtils;

/**
 * Exposes public methods that make {@code BlackjackEngine} testing significantly
 * easier, especially regarding splitting operations.
 *
 * @author kqfall1
 * @since 23/12/2025
 */
public final class TestDeck extends Deck
{
	public Hand drawPair(Rank rank)
	{
		final var hand = new Hand();
		hand.addCard(drawRank(rank));
		hand.addCard(drawRank(rank));
		return hand;
	}

	public Card drawRank(Rank rank)
	{
		assert rank != null : "rank == null";

		for (Card card : super.getCards())
		{
			if (card.getRank() == rank)
			{
				super.cards.remove(card);
				return card;
			}
		}

		throw new IllegalStateException(String.format(
			"The deck does not contain another %s.",
			StringUtils.normalizeLower(rank.toString())
		));
	}
}