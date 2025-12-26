package com.github.kqfall1.java.blackjackEngine.controllers;

import com.github.kqfall1.java.blackjackEngine.model.cards.Card;
import com.github.kqfall1.java.blackjackEngine.model.cards.Rank;
import com.github.kqfall1.java.blackjackEngine.model.cards.Suit;
import com.github.kqfall1.java.blackjackEngine.model.cards.TestDeck;
import com.github.kqfall1.java.blackjackEngine.model.engine.EngineState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import java.util.ArrayDeque;
import java.util.List;

/**
 * Provides abstraction for creating tests that require the use of a
 * {@code TestDeck} to guarantee certain game actions are possible.
 *
 * @author kqfall1
 * @since 23/12/2025
 */
public abstract class CustomDeckTest extends EngineTestTemplate
{
	public static final int MAXIMUM_SPLIT_COUNT = 3;
	public TestDeck testDeck;

	public void advanceToDealerTurn() throws Exception
	{
		super.engine.placeHandBet(super.randomBetAmount(super.engine.getPlayer().getChips()));
		super.advanceToPlayerTurn();

		if (super.engine.getState() == EngineState.PLAYER_TURN)
		{
			super.engine.playerStand();
		}
	}

	public void initCardsForDealerBust()
	{
		testDeck = new TestDeck();
		testDeck.setInitialCards(new ArrayDeque<>(List.of(
			new Card(Rank.TEN, Suit.SPADE),
			new Card(Rank.TEN, Suit.DIAMOND),
			new Card(Rank.SEVEN, Suit.CLUB),
			new Card(Rank.SIX, Suit.HEART),
			new Card(Rank.SIX, Suit.SPADE)
		)));
	}

	public void initCardsForDealerWin()
	{
		testDeck = new TestDeck();
		testDeck.setInitialCards(new ArrayDeque<>(List.of(
			new Card(Rank.TEN, Suit.SPADE),
			new Card(Rank.TEN, Suit.DIAMOND),
			new Card(Rank.EIGHT, Suit.CLUB),
			new Card(Rank.SIX, Suit.HEART),
			new Card(Rank.THREE, Suit.DIAMOND)
		)));
	}

	public void initCardsForSplitting7s()
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

	public void initSplitHands() throws Exception
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