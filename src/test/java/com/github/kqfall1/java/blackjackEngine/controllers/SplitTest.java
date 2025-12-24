package com.github.kqfall1.java.blackjackEngine.controllers;

import com.github.kqfall1.java.blackjackEngine.model.cards.Card;
import com.github.kqfall1.java.blackjackEngine.model.cards.Rank;
import com.github.kqfall1.java.blackjackEngine.model.cards.Suit;
import com.github.kqfall1.java.blackjackEngine.model.cards.TestDeck;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import org.junit.jupiter.api.RepeatedTest;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.List;

/**
 * Provides abstraction for creating tests that verify the integrity of the
 * {@code BlackjackEngine} splitting mechanism.
 *
 * @author kqfall1
 * @since 23/12/2025
 */
abstract class SplitTest extends EngineTestTemplate
{
	TestDeck testDeck;

	void initCardsForPocket7s()
	{
		testDeck = new TestDeck();
		testDeck.setInitialCards(new ArrayDeque<>(List.of(
			new Card(Rank.SEVEN, Suit.CLUB),
			new Card(Rank.JACK, Suit.SPADE),
			new Card(Rank.SEVEN, Suit.HEART),
			new Card(Rank.THREE, Suit.DIAMOND)
		)));
	}

	@RepeatedTest(TEST_ITERATIONS)
	abstract void main() throws Exception;

	@Override
	void start() throws InsufficientChipsException, IOException
	{
		if (testDeck != null)
		{
			super.engine.getDealer().setDeck(testDeck);
		}

		super.start();
	}
}