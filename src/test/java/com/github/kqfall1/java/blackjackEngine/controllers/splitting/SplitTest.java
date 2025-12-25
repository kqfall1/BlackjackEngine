package com.github.kqfall1.java.blackjackEngine.controllers.splitting;

import com.github.kqfall1.java.blackjackEngine.controllers.EngineTestTemplate;
import com.github.kqfall1.java.blackjackEngine.model.cards.Card;
import com.github.kqfall1.java.blackjackEngine.model.cards.Rank;
import com.github.kqfall1.java.blackjackEngine.model.cards.Suit;
import com.github.kqfall1.java.blackjackEngine.model.cards.TestDeck;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
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
	static final int MAXIMUM_SPLIT_COUNT = 3;
	TestDeck testDeck;

	void initCardsForSplitting7s()
	{
		testDeck = new TestDeck();
		testDeck.setInitialCards(new ArrayDeque<>(List.of(
			new Card(Rank.SEVEN, Suit.CLUB),
			new Card(Rank.JACK, Suit.SPADE),
			new Card(Rank.SEVEN, Suit.HEART),
			new Card(Rank.THREE, Suit.DIAMOND),
			new Card(Rank.NINE, Suit.CLUB),
			new Card(Rank.SEVEN, Suit.DIAMOND),
			new Card(Rank.KING, Suit.HEART),
			new Card(Rank.SEVEN, Suit.SPADE)
		)));
	}

	void initSplitHands() throws Exception
	{
		final var BET_AMOUNT = super.engine.getActiveHandContext().getBet().getAmount();
		var previousChipAmount = super.engine.getPlayer().getChips();

		for (int count = 0; count < MAXIMUM_SPLIT_COUNT; count++)
		{
			Assertions.assertFalse(super.engine.getActiveHandContext().isAltered());
			Assertions.assertTrue(super.engine.getActiveHandContext().getHand().isPocketPair());
			super.engine.playerSplit();

			Assertions.assertEquals(
				previousChipAmount.subtract(BET_AMOUNT),
				super.engine.getPlayer().getChips()
			);

			previousChipAmount = super.engine.getPlayer().getChips();
		}
	}

	@RepeatedTest(TEST_ITERATIONS)
	public abstract void main() throws Exception;
}