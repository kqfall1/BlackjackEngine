package com.github.kqfall1.java.blackjackEngine.model.cards;

import com.github.kqfall1.java.blackjackEngine.model.engine.StandardRuleConfig;
import com.github.kqfall1.java.blackjackEngine.model.entities.Dealer;
import com.github.kqfall1.java.blackjackEngine.model.hands.Hand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import com.github.kqfall1.java.managers.InputManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

/**
 * Tests the getter methods of {@code Dealer}, {@code Hand}, {@code Player}, and
 * {@code PlayerHand} objects. Also verifies the functionalities of all those aforementioned.
 *
 * @author kqfall1
 * @since 21/12/2025
 */
public final class EntityAndHandSubsystemTest
{
	private Hand blackjack;
	private Dealer dealer;
	private Hand dealerHand;
	private static final int INITIAL_CHIP_AMOUNT = 5000;
	private Hand pHand;
	private Hand pocketPair = new Hand();
	private Deck sideDeck;
	private static final int TEST_ITERATIONS = 1000;

	@BeforeEach
	public void init()
	{
		blackjack = randomBlackjack();
		dealer = new Dealer();
		dealerHand = new Hand();
		pHand = new Hand();
		pocketPair = randomPocketPair();
		sideDeck = new Deck();
	}

	private void dealerTest()
	{
		final var PREVIOUS_HAND = dealer.getHand();
		final var PREVIOUS_DECK = dealer.getDeck();
		dealer.setHand(dealerHand);
		dealer.setDeck(sideDeck);
		Assertions.assertNotEquals(PREVIOUS_HAND, dealer.getHand());
		Assertions.assertNotEquals(PREVIOUS_DECK, dealer.getDeck());
		final int PREVIOUS_DEALER_HAND_SIZE = dealer.getHand().getCards().size();
		final int PREVIOUS_DECK_SIZE = dealer.getDeck().getCards().size();
		dealer.getHand().addCard(dealer.hit());
		Assertions.assertTrue(dealer.getHand().getCards().size() > PREVIOUS_DEALER_HAND_SIZE);
		Assertions.assertTrue(dealer.getDeck().getCards().size() > PREVIOUS_DECK_SIZE);
	}

	private void _handTest(int expectedSize)
	{
		InputManager.validateNumber(
			expectedSize,
			"expectedSize",
			0,
			Float.MAX_VALUE
		);
		assertEquals(expectedSize, dealerHand.getCards().size());
		assertEquals(expectedSize, pHand.getCards().size());
		assertEquals(dealerHand, pHand);
		assertEquals(dealerHand.toString(), pHand.toString());
		assertEquals(dealerHand.toStringPretty(), pHand.toStringPretty());
	}

	private void handTest()
	{
		_handTest(0);
		pHand.addCard(sideDeck.draw());
		dealerHand.addCard(sideDeck.draw());
		Assertions.assertNotEquals(dealerHand, pHand);
		pHand.removeCard(0);
		dealerHand.removeCard(0);
		final var newCard = sideDeck.draw();
		dealerHand.addCard(newCard);
		pHand.addCard(newCard);
		_handTest(1);

		Assertions.assertTrue(blackjack.isBlackjack());
		Assertions.assertTrue(pocketPair.isPocketPair());

		final var bustHand = new Hand();
		while (bustHand.getScore() <= StandardRuleConfig.TOP_SCORE)
		{
			bustHand.addCard(sideDeck.draw());
		}
		Assertions.assertTrue(bustHand.isBusted());
	}

	private void playerHandTest()
	{

	}

	private void playerTest()
	{

	}

	private Hand randomBlackjack()
	{
		final var ACE = new Card(Rank.ACE, randomSuit());
		final var BLACKJACK_HAND = new Hand();
		final int MAX_ORDINAL = Rank.values().length - 1;
		final int MIN_ORDINAL = Rank.TEN.ordinal();
		final int RANDOM_RANK_INDEX = (int) (Math.random() *
			(MAX_ORDINAL - MIN_ORDINAL) + MIN_ORDINAL);

		BLACKJACK_HAND.addCard(ACE);
		BLACKJACK_HAND.addCard(new Card(
			Rank.values()[RANDOM_RANK_INDEX],
			randomSuit()
		));

		return BLACKJACK_HAND;
	}

	private Hand randomPocketPair()
	{
		final var POCKET_PAIR = new Hand();
		final int RANDOM_RANK_INDEX = (int) (Math.random() * Rank.values().length);

		POCKET_PAIR.addCard(new Card(
			Rank.values()[RANDOM_RANK_INDEX],
			Suit.values()[0]
		));
		POCKET_PAIR.addCard(new Card(
			Rank.values()[RANDOM_RANK_INDEX],
			Suit.values()[1]
		));

		return POCKET_PAIR;
	}

	private Suit randomSuit()
	{
		return Suit.values()[(int) (Math.random() * Suit.values().length)];
	}

	@RepeatedTest(TEST_ITERATIONS)
	public void testEntityAndHandSubsystem()
	{
		handTest();
		playerHandTest();
		playerTest();
	}
}