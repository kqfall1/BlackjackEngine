package com.github.kqfall1.java.blackjackEngine.model;

import com.github.kqfall1.java.blackjackEngine.model.cards.*;
import com.github.kqfall1.java.blackjackEngine.model.engine.StandardRuleConfig;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.NoMoreCardsException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

public final class CardSubsystemTest
{
	private static final Card ACE_OF_CLUBS_1 = new Card(Rank.ACE, Suit.CLUB);
	private static Card ACE_OF_CLUBS_2;
	private static Deck deck;
	private static Set<Card> drawnCards;
	private static TestDeck testDeck;
	private static final int TEST_ITERATIONS = 5000;
	private static final Card TWO_OF_DIAMONDS = new Card(Rank.TWO, Suit.DIAMOND);

	@BeforeEach
	void init()
	{
		ACE_OF_CLUBS_2 = ACE_OF_CLUBS_1;
		deck = new Deck();
		drawnCards = new HashSet<>();
		testDeck = new TestDeck();
		testDeck.setInitialCards(new ArrayDeque<>(List.of(ACE_OF_CLUBS_1, TWO_OF_DIAMONDS)));
	}

	@RepeatedTest(TEST_ITERATIONS)
	void main()
	{
		assertEquals(ACE_OF_CLUBS_1, ACE_OF_CLUBS_2);
		assertEquals(ACE_OF_CLUBS_1.toString(), ACE_OF_CLUBS_2.toString());
		assertEquals(ACE_OF_CLUBS_1.toStringPretty(), ACE_OF_CLUBS_2.toStringPretty());
		assertEquals(Rank.ACE, ACE_OF_CLUBS_1.getRank());
		assertEquals(Suit.CLUB, ACE_OF_CLUBS_1.getSuit());
		assertEquals(StandardRuleConfig.FULL_DECK_CARD_COUNT, deck.getCards().size());

		assertEquals(ACE_OF_CLUBS_1, testDeck.draw());
		assertEquals(TWO_OF_DIAMONDS, testDeck.draw());
		Assertions.assertTrue(!testDeck.getCards().contains(ACE_OF_CLUBS_1)
			&&  !testDeck.getCards().contains(TWO_OF_DIAMONDS));

		for (int count = 0; count < StandardRuleConfig.FULL_DECK_CARD_COUNT + 1; count++)
		{
			try
			{
				final var card = deck.draw();
				assertEquals(
				StandardRuleConfig.FULL_DECK_CARD_COUNT - count - 1,
					deck.getCards().size()
				);
				Assertions.assertFalse(deck.getCards().contains(card));
				Assertions.assertFalse(drawnCards.contains(card));
				drawnCards.add(card);
			}
			catch (NoMoreCardsException e)
			{
				assertEquals(StandardRuleConfig.FULL_DECK_CARD_COUNT, count);
			}
		}
	}
}