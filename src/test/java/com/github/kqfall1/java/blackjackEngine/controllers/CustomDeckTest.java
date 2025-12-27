package com.github.kqfall1.java.blackjackEngine.controllers;

import com.github.kqfall1.java.blackjackEngine.model.cards.*;
import com.github.kqfall1.java.blackjackEngine.model.engine.EngineState;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import java.io.IOException;
import java.math.BigDecimal;
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
	public TestDeck randomCards;
	public TestDeck testDeck;

	public CustomDeckTest()
	{
		randomCards = new TestDeck();
		testDeck = new TestDeck();
	}

	public void advanceToDealerTurn(BigDecimal maximumBetAmount) throws Exception
	{
		advanceToPlayerTurn(maximumBetAmount);

		if (super.engine.getState() == EngineState.PLAYER_TURN)
		{
			super.engine.playerStand();
		}
	}

	public void advanceToPlayerTurn(BigDecimal maximumBetAmount) throws Exception
	{
		super.placeRandomHandBet(maximumBetAmount);
		super.engine.deal();
		super.engine.advanceAfterDeal();
		super.declinePossibleInsuranceBet();
	}

	@BeforeEach
	public abstract void init() throws InsufficientChipsException, IOException;

	public void initCardsForBust1()
	{
		testDeck.setInitialCards(new ArrayDeque<>(List.of(
			new Card(Rank.TEN, Suit.SPADE),
			new Card(Rank.TEN, Suit.DIAMOND),
			new Card(Rank.SEVEN, Suit.CLUB),
			new Card(Rank.SIX, Suit.HEART),
			new Card(Rank.SIX, Suit.SPADE)
		)));
	}

	public void initCardsForBust2()
	{
		testDeck.setInitialCards(new ArrayDeque<>(List.of(
			new Card(Rank.FIVE, Suit.HEART),
			new Card(Rank.QUEEN, Suit.DIAMOND),
			new Card(Rank.KING, Suit.DIAMOND),
			new Card(Rank.TWO, Suit.DIAMOND),
			new Card(Rank.JACK, Suit.CLUB)
		)));
	}

	public void initCardsForBust3()
	{
		testDeck.setInitialCards(new ArrayDeque<>(List.of(
			new Card(Rank.NINE, Suit.SPADE),
			new Card(Rank.EIGHT, Suit.SPADE),
			new Card(Rank.FOUR, Suit.CLUB),
			new Card(Rank.FIVE, Suit.DIAMOND),
			new Card(Rank.QUEEN, Suit.CLUB)
		)));
	}

	public void initCardsForDealerWin1()
	{
		testDeck.setInitialCards(new ArrayDeque<>(List.of(
			new Card(Rank.TEN, Suit.SPADE),
			new Card(Rank.TEN, Suit.DIAMOND),
			new Card(Rank.EIGHT, Suit.CLUB),
			new Card(Rank.SIX, Suit.HEART),
			new Card(Rank.THREE, Suit.DIAMOND)
		)));
	}

	public void initCardsForDealerWin2()
	{
		testDeck.setInitialCards(new ArrayDeque<>(List.of(
			new Card(Rank.NINE, Suit.DIAMOND),
			new Card(Rank.TEN, Suit.DIAMOND),
			new Card(Rank.JACK, Suit.CLUB),
			new Card(Rank.FOUR, Suit.HEART),
			new Card(Rank.SEVEN, Suit.DIAMOND)
		)));
	}

	public void initCardsForInsurance()
	{
		final var RANDOM_ACE = new Card(
			Rank.ACE,
			Suit.values()[(int) (Suit.values().length * Math.random())]
		);
		randomCards = new TestDeck();
		randomCards.removeCards(RANDOM_ACE);

		testDeck.setInitialCards(new ArrayDeque<>(List.of(
			randomCards.draw(),
			RANDOM_ACE,
			randomCards.draw(),
			randomCards.draw()
		)));
	}

	public void initCardsForPlayerWin1()
	{
		testDeck.setInitialCards(new ArrayDeque<>(List.of(
			new Card(Rank.TEN, Suit.SPADE),
			new Card(Rank.TEN, Suit.DIAMOND),
			new Card(Rank.NINE, Suit.HEART),
			new Card(Rank.EIGHT, Suit.CLUB)
		)));
	}

	public void initCardsForPlayerWin2()
	{
		testDeck.setInitialCards(new ArrayDeque<>(List.of(
			new Card(Rank.KING, Suit.SPADE),
			new Card(Rank.FOUR, Suit.CLUB),
			new Card(Rank.JACK, Suit.SPADE),
			new Card(Rank.THREE, Suit.SPADE),
			new Card(Rank.THREE, Suit.DIAMOND),
			new Card(Rank.EIGHT, Suit.CLUB)
		)));
	}

	public void initCardsForPush1()
	{
		testDeck.setInitialCards(new ArrayDeque<>(List.of(
			new Card(Rank.QUEEN, Suit.CLUB),
			new Card(Rank.KING, Suit.HEART),
			new Card(Rank.JACK, Suit.CLUB),
			new Card(Rank.JACK, Suit.DIAMOND)
		)));
	}

	public void initCardsForPush2()
	{
		testDeck.setInitialCards(new ArrayDeque<>(List.of(
			new Card(Rank.ACE, Suit.HEART),
			new Card(Rank.EIGHT, Suit.CLUB),
			new Card(Rank.SIX, Suit.DIAMOND),
			new Card(Rank.FOUR, Suit.DIAMOND),
			new Card(Rank.FIVE, Suit.SPADE)
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
				previousChipAmount.subtract(BET_AMOUNT).stripTrailingZeros(),
				super.engine.getPlayer().getChips()
			);

			previousChipAmount = super.engine.getPlayer().getChips();
		}
	}

	public void initCardsForSplitting7s()
	{
		final var SEVEN_OF_CLUBS = new Card(Rank.SEVEN, Suit.CLUB);
		final var SEVEN_OF_DIAMONDS = new Card(Rank.SEVEN, Suit.DIAMOND);
		final var SEVEN_OF_HEARTS = new Card(Rank.SEVEN, Suit.HEART);
		final var SEVEN_OF_SPADES = new Card(Rank.SEVEN, Suit.SPADE);

		randomCards = new TestDeck();
		randomCards.removeCards(SEVEN_OF_CLUBS, SEVEN_OF_DIAMONDS, SEVEN_OF_HEARTS, SEVEN_OF_CLUBS);

		testDeck.setInitialCards(new ArrayDeque<>(List.of(
			SEVEN_OF_CLUBS,
			randomCards.draw(),
			SEVEN_OF_DIAMONDS,
			randomCards.draw(),
			randomCards.draw(),
			SEVEN_OF_HEARTS,
			randomCards.draw(),
			SEVEN_OF_SPADES
		)));
	}

	@RepeatedTest(TEST_ITERATIONS)
	public abstract void main() throws Exception;
}