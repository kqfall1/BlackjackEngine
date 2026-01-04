package com.github.kqfall1.java.blackjackEngine.model;

import com.github.kqfall1.java.blackjackEngine.model.betting.Shoe;
import com.github.kqfall1.java.blackjackEngine.model.cards.*;
import com.github.kqfall1.java.blackjackEngine.model.engine.BlackjackConstants;
import com.github.kqfall1.java.blackjackEngine.model.enums.Rank;
import com.github.kqfall1.java.blackjackEngine.model.enums.Suit;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.NoMoreCardsException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

public final class CardSubsystemTest
{
	private static final Card ACE_OF_CLUBS_1 = new Card(Rank.ACE, Suit.CLUB);
	private static Card ACE_OF_CLUBS_2;
	private static final double CUTOFF_PERCENTAGE_RANGE =
		Shoe.MAXIMUM_CUTOFF_PERCENTAGE_NUMERATOR - Shoe.MINIMUM_CUTOFF_PERCENTAGE_NUMERATOR;
	private static Deck deck1;
	private static Set<Card> drawnCards;
	private static double randomCutoffPercentageNumerator;
	private static Shoe shoe1;
	private static Shoe shoe2;
	private static final int SHOE_DECK_AMOUNT = 8;
	private static TestDeck testDeck;
	private static final int TEST_ITERATIONS = 5000;
	private static final Card TWO_OF_DIAMONDS = new Card(Rank.TWO, Suit.DIAMOND);

	@BeforeEach
	void init()
	{
		ACE_OF_CLUBS_2 = ACE_OF_CLUBS_1;
		deck1 = new Deck();
		drawnCards = new HashSet<>();
		randomCutoffPercentageNumerator
			= Math.random() * CUTOFF_PERCENTAGE_RANGE + Shoe.MINIMUM_CUTOFF_PERCENTAGE_NUMERATOR;
		shoe1 = new Shoe(randomCutoffPercentageNumerator, SHOE_DECK_AMOUNT);
		shoe2 = new Shoe(randomCutoffPercentageNumerator, SHOE_DECK_AMOUNT);
		testDeck = new TestDeck();
		testDeck.setInitialCards(new ArrayDeque<>(List.of(ACE_OF_CLUBS_1, TWO_OF_DIAMONDS)));
	}

	@RepeatedTest(TEST_ITERATIONS)
	void main()
	{
		deckTest();
		shoeTest();
	}

	private void deckTest()
	{
		assertEquals(ACE_OF_CLUBS_1, ACE_OF_CLUBS_2);
		assertEquals(ACE_OF_CLUBS_1.toString(), ACE_OF_CLUBS_2.toString());
		assertEquals(ACE_OF_CLUBS_1.toStringPretty(), ACE_OF_CLUBS_2.toStringPretty());
		assertEquals(Rank.ACE, ACE_OF_CLUBS_1.getRank());
		assertEquals(Suit.CLUB, ACE_OF_CLUBS_1.getSuit());

		assertEquals(BlackjackConstants.DEFAULT_FULL_DECK_CARD_COUNT, deck1.getCards().size());
		assertEquals(ACE_OF_CLUBS_1, testDeck.draw());
		assertEquals(TWO_OF_DIAMONDS, testDeck.draw());
		assertTrue(!testDeck.getCards().contains(ACE_OF_CLUBS_1)
			&&  !testDeck.getCards().contains(TWO_OF_DIAMONDS));

		for (int count = 0; count < BlackjackConstants.DEFAULT_FULL_DECK_CARD_COUNT + 1; count++)
		{
			try
			{
				final var card = deck1.draw();
				assertEquals(
				BlackjackConstants.DEFAULT_FULL_DECK_CARD_COUNT - count - 1,
					deck1.getCards().size()
				);
				Assertions.assertFalse(deck1.getCards().contains(card));
				Assertions.assertFalse(drawnCards.contains(card));
				drawnCards.add(card);
			}
			catch (NoMoreCardsException e)
			{
				assertEquals(BlackjackConstants.DEFAULT_FULL_DECK_CARD_COUNT, count);
			}
		}
	}

	private void shoeTest()
	{
		assertEquals(
			shoe1.getCutoffPercentageNumerator() / Shoe.ONE_HUNDRED,
			shoe1.getCutoffPercentage()
		);
		assertEquals(
			shoe2.getCutoffPercentageNumerator() / Shoe.ONE_HUNDRED,
			shoe2.getCutoffPercentage()
		);
		assertEquals(randomCutoffPercentageNumerator, shoe1.getCutoffPercentageNumerator());
		assertEquals(
			shoe1.getCutoffPercentageNumerator(),
			shoe2.getCutoffPercentageNumerator()
		);
		assertEquals(
			BlackjackConstants.DEFAULT_FULL_DECK_CARD_COUNT * SHOE_DECK_AMOUNT,
			shoe1.getCards().size()
		);
		assertEquals(SHOE_DECK_AMOUNT, shoe1.getNumberOfDecks());
		assertEquals(shoe1.getNumberOfDecks(), shoe2.getNumberOfDecks());

		final var CARD_COUNT = BlackjackConstants.DEFAULT_FULL_DECK_CARD_COUNT * SHOE_DECK_AMOUNT;
		assertEquals(CARD_COUNT, shoe1.getCards().size());

		for (int count = 0; count < CARD_COUNT + 1; count++)
		{
			try
			{
				shoe1.draw();
				assertEquals(
					CARD_COUNT - count - 1,
					shoe1.getCards().size()
				);

				if (shoe1.getCards().size() > shoe1.getCutoffAmount())
				{
					Assertions.assertFalse(shoe1.isShufflingRequired());
				}
				else
				{
					assertTrue(shoe1.isShufflingRequired());
				}
			}
			catch (NoMoreCardsException e)
			{
				assertEquals(CARD_COUNT, count);
			}
		}
	}
}