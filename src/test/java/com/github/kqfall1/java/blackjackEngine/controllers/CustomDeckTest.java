package com.github.kqfall1.java.blackjackEngine.controllers;

import com.github.kqfall1.java.blackjackEngine.model.cards.*;
import com.github.kqfall1.java.blackjackEngine.model.engine.StandardRuleConfig;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import com.github.kqfall1.java.blackjackEngine.model.hands.Hand;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayDeque;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

/**
 * Provides abstraction for creating tests that require the use of a
 * {@code TestDeck} to guarantee certain game actions are possible.
 *
 * @author kqfall1
 * @since 23/12/2025
 */
public abstract class CustomDeckTest extends EngineTest
{
	private static final int BUST_METHOD_COUNT = 3;
	public static final int MAXIMUM_SPLIT_COUNT = 3;
	public TestDeck randomCards;
	public static final int SHOWDOWN_BLACKJACK_DEALER_METHOD_COUNT = 2;
	public static final int SHOWDOWN_BLACKJACK_METHOD_COUNT = 4;
	public static final int SHOWDOWN_BLACKJACK_PLAYER_METHOD_COUNT = 2;
	public static final int SHOWDOWN_NORMAL_METHOD_COUNT = 6;
	public static final int SHOWDOWN_NORMAL_DEALER_WIN_METHOD_COUNT = 2;
	public static final int SHOWDOWN_NORMAL_PLAYER_WIN_METHOD_COUNT = 2;
	public TestDeck testDeck;

	public static final BigDecimal DOUBLE_DOWN_TEST_MAXIMUM_INITIAL_BET_AMOUNT
		= INITIAL_PLAYER_CHIP_AMOUNT.divide(
			BigDecimal.valueOf((MAXIMUM_SPLIT_COUNT + 2) * 2),
			MathContext.DECIMAL128
		);
	public static final BigDecimal SPLIT_TEST_MAXIMUM_INITIAL_BET_AMOUNT
		= INITIAL_PLAYER_CHIP_AMOUNT.divide(
			BigDecimal.valueOf(MAXIMUM_SPLIT_COUNT + 2),
			MathContext.DECIMAL128
		);

	public CustomDeckTest()
	{
		randomCards = new TestDeck();
		testDeck = new TestDeck();
	}

	@BeforeEach
	public abstract void init() throws InsufficientChipsException, IOException;

	public final int _initCardsForBlackjack()
	{
		final var BLACKJACK_METHOD_INDEX =
			(int) (Math.random() * SHOWDOWN_BLACKJACK_METHOD_COUNT);

		Hand testHand = new Hand();
		testHand.addCards(randomCards.draw(), randomCards.draw());

		while (testHand.isBlackjack())
		{
			testHand.removeCard(StandardRuleConfig.INITIAL_CARD_COUNT - 1);
			testHand.addCards(randomCards.draw());
		}

		final var CARD_1 = testHand.getCards().getFirst();
		final var CARD_2 = testHand.getCards().getLast();

		switch (BLACKJACK_METHOD_INDEX)
		{
			case 0 -> initCardsForDealerBlackjack1(CARD_1, CARD_2);
			case 1 -> initCardsForDealerBlackjack2(CARD_1, CARD_2);
			case 2 -> initCardsForPlayerBlackjack1(CARD_1, CARD_2);
			case 3 -> initCardsForPlayerBlackjack2(CARD_1, CARD_2);
			case 4 -> initBlackjackPush1();
			case 5 -> initBlackjackPush2();
		}

		return BLACKJACK_METHOD_INDEX;
	}

	private void initBlackjackPush1()
	{
		testDeck.setInitialCards(new ArrayDeque<>(List.of(
			randomCards.removeCardOfRank(Rank.QUEEN),
			randomCards.removeCardOfRank(Rank.ACE),
			randomCards.removeCardOfRank(Rank.ACE),
			randomCards.removeCardOfRank(Rank.TEN)
		)));
	}

	private void initBlackjackPush2()
	{
		testDeck.setInitialCards(new ArrayDeque<>(List.of(
			randomCards.removeCardOfRank(Rank.ACE),
			randomCards.removeCardOfRank(Rank.ACE),
			randomCards.removeCardOfRank(Rank.KING),
			randomCards.removeCardOfRank(Rank.JACK)
		)));
	}

	public final int _initCardsForBust()
	{
		final var BUST_METHOD_INDEX = (int) (Math.random() * BUST_METHOD_COUNT);

		switch (BUST_METHOD_INDEX)
		{
			case 0 -> initCardsForBust1();
			case 1 -> initCardsForBust2();
			case 2 -> initCardsForBust3();
		}

		return BUST_METHOD_INDEX;
	}

	private void initCardsForBust1()
	{
		testDeck.setInitialCards(new ArrayDeque<>(List.of(
			randomCards.removeCardOfRank(Rank.TEN),
			randomCards.removeCardOfRank(Rank.TEN),
			randomCards.removeCardOfRank(Rank.SEVEN),
			randomCards.removeCardOfRank(Rank.SIX),
			randomCards.removeCardOfRank(Rank.SIX)
		)));
	}

	private void initCardsForBust2()
	{
		testDeck.setInitialCards(new ArrayDeque<>(List.of(
			randomCards.removeCardOfRank(Rank.FIVE),
			randomCards.removeCardOfRank(Rank.QUEEN),
			randomCards.removeCardOfRank(Rank.KING),
			randomCards.removeCardOfRank(Rank.TWO),
			randomCards.removeCardOfRank(Rank.JACK)
		)));
}

	private void initCardsForBust3()
	{
		testDeck.setInitialCards(new ArrayDeque<>(List.of(
			randomCards.removeCardOfRank(Rank.NINE),
			randomCards.removeCardOfRank(Rank.EIGHT),
			randomCards.removeCardOfRank(Rank.FOUR),
			randomCards.removeCardOfRank(Rank.FIVE),
			randomCards.removeCardOfRank(Rank.NINE)
		)));
	}

	public void initCardsForDealerSoft17()
	{
		testDeck.setInitialCards(new ArrayDeque<>(List.of(
			randomCards.draw(),
			randomCards.removeCardOfRank(Rank.SIX),
			randomCards.draw(),
			randomCards.removeCardOfRank(Rank.ACE)
		)));
	}

	private void initCardsForDealerBlackjack1 (Card playerCard1, Card playerCard2)
	{
		testDeck.setInitialCards(new ArrayDeque<>(List.of(
			playerCard1,
			randomCards.removeCardOfRank(Rank.ACE),
			playerCard2,
			randomCards.removeCardOfRank(Rank.JACK)
		)));
	}

	private void initCardsForDealerBlackjack2(Card playerCard1, Card playerCard2)
	{
		testDeck.setInitialCards(new ArrayDeque<>(List.of(
			playerCard1,
			randomCards.removeCardOfRank(Rank.TEN),
			playerCard2,
			randomCards.removeCardOfRank(Rank.ACE)
		)));
	}

	private void initCardsForDealerWin1()
	{
		testDeck.setInitialCards(new ArrayDeque<>(List.of(
			randomCards.removeCardOfRank(Rank.TEN),
			randomCards.removeCardOfRank(Rank.TEN),
			randomCards.removeCardOfRank(Rank.EIGHT),
			randomCards.removeCardOfRank(Rank.SIX),
			randomCards.removeCardOfRank(Rank.THREE)
		)));
	}

	private void initCardsForDealerWin2()
	{
		testDeck.setInitialCards(new ArrayDeque<>(List.of(
			randomCards.removeCardOfRank(Rank.NINE),
			randomCards.removeCardOfRank(Rank.TEN),
			randomCards.removeCardOfRank(Rank.JACK),
			randomCards.removeCardOfRank(Rank.FOUR),
			randomCards.removeCardOfRank(Rank.SEVEN)
		)));
	}

	public final void initCardsForInsurance()
	{
		testDeck.setInitialCards(new ArrayDeque<>(List.of(
			randomCards.draw(),
			randomCards.removeCardOfRank(Rank.ACE),
			randomCards.draw(),
			randomCards.draw()
		)));
	}

	public final void initCardsForInsuranceAndSplitting(Rank rank)
	{
		Assertions.assertNotNull(rank);
		final var SPLIT_CARD_1 = randomCards.removeCardOfRank(rank);
		final var SPLIT_CARD_2 = randomCards.removeCardOfRank(rank);
		final var SPLIT_CARD_3 = randomCards.removeCardOfRank(rank);
		final var SPLIT_CARD_4 = randomCards.removeCardOfRank(rank);

		testDeck.setInitialCards(new ArrayDeque<>(List.of(
			SPLIT_CARD_1,
			randomCards.removeCardOfRank(Rank.ACE),
			SPLIT_CARD_2,
			randomCards.draw(),
			randomCards.draw(),
			SPLIT_CARD_3,
			randomCards.draw(),
			SPLIT_CARD_4
		)));
	}

	public final int _initCardsForNormalShowdown()
	{
		final var SHOWDOWN_METHOD_INDEX = (int) (Math.random() * SHOWDOWN_NORMAL_METHOD_COUNT);

		switch (SHOWDOWN_METHOD_INDEX)
		{
			case 0 -> initCardsForDealerWin1();
			case 1 -> initCardsForDealerWin2();
			case 2 -> initCardsForPlayerWin1();
			case 3 -> initCardsForPlayerWin2();
			case 4 -> initCardsForPush1();
			case 5 -> initCardsForPush2();
		}

		return SHOWDOWN_METHOD_INDEX;
	}

	private void initCardsForPlayerBlackjack1(Card dealerCard1, Card dealerCard2)
	{
		testDeck.setInitialCards(new ArrayDeque<>(List.of(
			randomCards.removeCardOfRank(Rank.ACE),
			dealerCard1,
			randomCards.removeCardOfRank(Rank.KING),
			dealerCard2
		)));
	}

	private void initCardsForPlayerBlackjack2(Card dealerCard1, Card dealerCard2)
	{
		testDeck.setInitialCards(new ArrayDeque<>(List.of(
			randomCards.removeCardOfRank(Rank.QUEEN),
			dealerCard1,
			randomCards.removeCardOfRank(Rank.ACE),
			dealerCard2
		)));
	}

	public final void initCardsForPlayerWin1()
	{
		testDeck.setInitialCards(new ArrayDeque<>(List.of(
			randomCards.removeCardOfRank(Rank.TEN),
			randomCards.removeCardOfRank(Rank.TEN),
			randomCards.removeCardOfRank(Rank.NINE),
			randomCards.removeCardOfRank(Rank.EIGHT)
		)));
	}

	public final void initCardsForPlayerWin2()
	{
		testDeck.setInitialCards(new ArrayDeque<>(List.of(
			randomCards.removeCardOfRank(Rank.KING),
			randomCards.removeCardOfRank(Rank.FOUR),
			randomCards.removeCardOfRank(Rank.JACK),
			randomCards.removeCardOfRank(Rank.THREE),
			randomCards.removeCardOfRank(Rank.THREE),
			randomCards.removeCardOfRank(Rank.EIGHT)
		)));
	}

	public final void initCardsForPush1()
	{
		testDeck.setInitialCards(new ArrayDeque<>(List.of(
			randomCards.removeCardOfRank(Rank.QUEEN),
			randomCards.removeCardOfRank(Rank.KING),
			randomCards.removeCardOfRank(Rank.JACK),
			randomCards.removeCardOfRank(Rank.JACK)
		)));
	}

	public final void initCardsForPush2()
	{
		testDeck.setInitialCards(new ArrayDeque<>(List.of(
			randomCards.removeCardOfRank(Rank.ACE),
			randomCards.removeCardOfRank(Rank.EIGHT),
			randomCards.removeCardOfRank(Rank.SIX),
			randomCards.removeCardOfRank(Rank.FOUR),
			randomCards.removeCardOfRank(Rank.FIVE)
		)));
	}

	public final void initSplitHands() throws Exception
	{
		var previousChipAmount = super.engine.getPlayer().getChips();

		for (int count = 0;
			 count < MAXIMUM_SPLIT_COUNT;
			 count++)
		{
			Assertions.assertFalse(super.engine.getActiveHandContext().isAltered());
			Assertions.assertTrue(super.engine.getActiveHandContext().getHand().isPocketPair());
			super.engine.playerSplit();

			Assertions.assertTrue(
				nearlyEquals(
					previousChipAmount.subtract(
						super.engine.getActiveHandContext().getBet().getAmount()
					),
					super.engine.getPlayer().getChips(),
					StandardRuleConfig.CHIP_SCALE
				)
			);
			Assertions.assertFalse(super.engine.getPlayer().getContexts().get(
				super.engine.getActiveHandContextIndex() - 1).isAltered()
			);

			previousChipAmount = super.engine.getPlayer().getChips();
		}
	}

	public final void initCardsForSplitting (Rank rank)
	{
		Assertions.assertNotNull(rank);
		final var SPLIT_CARD_1 = randomCards.removeCardOfRank(rank);
		final var SPLIT_CARD_2 = randomCards.removeCardOfRank(rank);
		final var SPLIT_CARD_3 = randomCards.removeCardOfRank(rank);
		final var SPLIT_CARD_4 = randomCards.removeCardOfRank(rank);

		testDeck.setInitialCards(new ArrayDeque<>(List.of(
			SPLIT_CARD_1,
			randomCards.draw(),
			SPLIT_CARD_2,
			randomCards.draw(),
			randomCards.draw(),
			SPLIT_CARD_3,
			randomCards.draw(),
			SPLIT_CARD_4
		)));
	}

	@RepeatedTest(TEST_ITERATIONS)
	public abstract void main() throws Exception;
}