package com.github.kqfall1.java.blackjackEngine.model.cards;

import com.github.kqfall1.java.blackjackEngine.model.exceptions.EmptyDeckException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import java.util.*;

/**
 * Tests the getter methods of {@code Card} and {@code Deck} objects, as well as
 * verifying that {@code Deck} objects function in a reasonable manner.
 *
 * @author kqfall1
 * @since 20/12/2025
 */
public final class CardSubsystemTest
{
	private Card aceOfClubs1;
	private Card aceOfClubs2;
	private Deck deck;
	private static final int DECK_CARD_COUNT = Rank.values().length * Suit.values().length;
	private Set<Card> drawnCards;
	private static final int TEST_ITERATIONS = 1000;

	@BeforeEach
	public void init()
	{
		aceOfClubs1 = new Card(Rank.ACE, Suit.CLUB);
		aceOfClubs2 = aceOfClubs1;
		deck = new Deck();
		drawnCards = new HashSet<>();
	}

	@RepeatedTest(TEST_ITERATIONS)
	public void testCard()
	{
		Assertions.assertEquals(aceOfClubs1, aceOfClubs2);
		Assertions.assertEquals(aceOfClubs1.toString(), aceOfClubs2.toString());
		Assertions.assertEquals(aceOfClubs1.toStringPretty(), aceOfClubs2.toStringPretty());
		Assertions.assertEquals(Rank.ACE, aceOfClubs1.getRank());
		Assertions.assertEquals(Suit.CLUB, aceOfClubs1.getSuit());
		Assertions.assertEquals(DECK_CARD_COUNT, deck.getCards().size());

		for (int count = 0; count < DECK_CARD_COUNT + 1; count++)
		{
			try
			{
				final var card = deck.draw();
				Assertions.assertEquals(
					DECK_CARD_COUNT - count - 1,
					deck.getCards().size()
				);
				Assertions.assertFalse(deck.getCards().contains(card));
				Assertions.assertFalse(drawnCards.contains(card));
				drawnCards.add(card);
			}
			catch (EmptyDeckException e)
			{
				Assertions.assertEquals(DECK_CARD_COUNT, count);
			}
		}
	}
}