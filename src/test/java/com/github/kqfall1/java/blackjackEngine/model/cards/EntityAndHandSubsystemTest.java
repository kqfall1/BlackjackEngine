package com.github.kqfall1.java.blackjackEngine.model.cards;

import com.github.kqfall1.java.blackjackEngine.model.betting.Bet;
import com.github.kqfall1.java.blackjackEngine.model.engine.StandardRuleConfig;
import com.github.kqfall1.java.blackjackEngine.model.entities.Dealer;
import com.github.kqfall1.java.blackjackEngine.model.entities.Player;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import com.github.kqfall1.java.blackjackEngine.model.hands.Hand;
import com.github.kqfall1.java.blackjackEngine.model.hands.HandContext;
import com.github.kqfall1.java.blackjackEngine.model.hands.HandContextType;
import com.github.kqfall1.java.managers.InputManager;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import java.math.BigDecimal;

/**
 * Tests the getter methods of {@code Dealer}, {@code Hand}, {@code Player}, and
 * {@code HandContext} objects. Also verifies the functionalities of all those aforementioned.
 *
 * @author kqfall1
 * @since 21/12/2025
 */
public final class EntityAndHandSubsystemTest
{
	private final Bet BET = new Bet(BET_AND_POT_INITIAL_AMOUNT);
	private static final BigDecimal BET_AND_POT_INITIAL_AMOUNT = BigDecimal.valueOf(1000);
	private Hand blackjack;
	private Dealer dealer;
	private Hand dealerHand;
	private HandContext mainContext;
	private Player player1;
	private Player player2;
	private static final BigDecimal PLAYER_INITIAL_CHIP_AMOUNT = BigDecimal.valueOf(5000);
	private Hand playerMainHand;
	private Hand playerSplitHand;
	private Hand pocketPair = new Hand();
	private Deck sideDeck;
	private HandContext splitContext;
	private static final int TEST_ITERATIONS = 1000;

	@BeforeEach
	public void init() throws InsufficientChipsException
	{
		blackjack = randomBlackjack();
		dealer = new Dealer();
		dealerHand = new Hand();
		mainContext = new HandContext(BET, HandContextType.MAIN);
		player1 = new Player();
		player2 = new Player();
		playerMainHand = new Hand();
		playerSplitHand = new Hand();
		pocketPair = randomPocketPair();
		sideDeck = new Deck();
		splitContext = new HandContext(BET, HandContextType.SPLIT);
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
		Assertions.assertTrue(dealer.getDeck().getCards().size() < PREVIOUS_DECK_SIZE);
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
		assertEquals(expectedSize, playerMainHand.getCards().size());
		assertEquals(dealerHand, playerMainHand);
		assertEquals(dealerHand.toString(), playerMainHand.toString());
		assertEquals(dealerHand.toStringPretty(), playerMainHand.toStringPretty());
	}

	private void handTest()
	{
		_handTest(0);
		playerMainHand.addCard(sideDeck.draw());
		dealerHand.addCard(sideDeck.draw());
		Assertions.assertNotEquals(dealerHand, playerMainHand);
		playerMainHand.removeCard(0);
		dealerHand.removeCard(0);
		final var newCard = sideDeck.draw();
		dealerHand.addCard(newCard);
		playerMainHand.addCard(newCard);
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

	private void handContextTest()
	{
		assertEquals(mainContext, new HandContext(BET, HandContextType.MAIN));
		assertEquals(BET, mainContext.getBet());
		assertEquals(BigDecimal.ZERO, mainContext.getPot().getAmount());
		assertEquals(splitContext, new HandContext(BET, HandContextType.SPLIT));
		assertEquals(BET, splitContext.getBet());
		assertEquals(BigDecimal.ZERO, splitContext.getPot().getAmount());
		Assertions.assertFalse(mainContext.isAltered());
		Assertions.assertFalse(mainContext.hasSurrendered());
		Assertions.assertFalse(splitContext.isAltered());
		Assertions.assertFalse(splitContext.hasSurrendered());
		Assertions.assertNotEquals(mainContext.getType(), splitContext.getType());

		mainContext.getPot().addChips(BET_AND_POT_INITIAL_AMOUNT);
		splitContext.getPot().addChips(BET_AND_POT_INITIAL_AMOUNT);
		assertEquals(mainContext.getPot().getAmount(), splitContext.getPot().getAmount());
		mainContext.setBet(new Bet(BigDecimal.valueOf(100)));
		Assertions.assertNotEquals(mainContext.getBet(), splitContext.getBet());
		mainContext.setHasSurrendered();
		Assertions.assertTrue(mainContext.hasSurrendered());
		Assertions.assertNotEquals(mainContext.hasSurrendered(), splitContext.hasSurrendered());

		try
		{
			new Bet(BigDecimal.ZERO);
		}
		catch (AssertionError ignored) {}
	}

	private void playerTest() throws InsufficientChipsException
	{
		assertEquals(player1.getChips(), player2.getChips());
		assertEquals(player1.toString(), player1.toString());
		player1.addContext(mainContext);
		player1.setChips(PLAYER_INITIAL_CHIP_AMOUNT);
		player2.addContext(splitContext);
		player2.setChips(PLAYER_INITIAL_CHIP_AMOUNT);
		assertEquals(PLAYER_INITIAL_CHIP_AMOUNT, player1.getChips());
		assertEquals(PLAYER_INITIAL_CHIP_AMOUNT, player2.getChips());
		assertEquals(player1.getContexts().size(), player2.getContexts().size());
		player1.clearContexts();
		player2.clearContexts();
		assertEquals(player1.getContexts().size(), player2.getContexts().size());

		try
		{
			player1.setChips(BigDecimal.valueOf(-1));
		}
		catch (InsufficientChipsException ignored) {}
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
	public void testEntityAndHandSubsystem() throws InsufficientChipsException
	{
		handTest();
		dealerTest();
		handContextTest();
		playerTest();
	}
}