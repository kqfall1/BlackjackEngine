package com.github.kqfall1.java.blackjackEngine.model;

import com.github.kqfall1.java.blackjackEngine.model.betting.Bet;
import com.github.kqfall1.java.blackjackEngine.model.cards.*;
import com.github.kqfall1.java.blackjackEngine.model.engine.StandardRuleConfig;
import com.github.kqfall1.java.blackjackEngine.model.entities.Dealer;
import com.github.kqfall1.java.blackjackEngine.model.entities.Player;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import com.github.kqfall1.java.blackjackEngine.model.hands.Hand;
import com.github.kqfall1.java.blackjackEngine.model.hands.HandContext;
import com.github.kqfall1.java.blackjackEngine.model.hands.HandContextType;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

public final class EntityAndHandSubsystemTest
{
	private final Bet BET = new Bet(BET_AND_POT_INITIAL_AMOUNT);
	private static final BigDecimal BET_AND_POT_INITIAL_AMOUNT = BigDecimal.valueOf(1000);
	private Hand blackjack;
	private Dealer dealer;
	private Hand dealerHand;
	private HandContext mainContext;
	private Hand mainHand;
	private Player player1;
	private Player player2;
	private static final BigDecimal PLAYER_INITIAL_CHIP_AMOUNT = BigDecimal.valueOf(5000);
	private Hand pocketPair = new Hand();
	private Shoe shoe;
	private HandContext splitContext;
	private static final int TEST_ITERATIONS = 5000;

	private void dealerTest()
	{
		final var PREVIOUS_HAND = dealer.getHand();
		final var PREVIOUS_SOURCE = dealer.getCardSource();
		dealer.setHand(dealerHand);
		dealer.setCardSource(shoe);
		Assertions.assertNotEquals(PREVIOUS_HAND, dealer.getHand());
		Assertions.assertNotEquals(PREVIOUS_SOURCE, dealer.getCardSource());
		final int PREVIOUS_DEALER_HAND_SIZE = dealer.getHand().getCards().size();
		dealer.getHand().addCards(dealer.hit());
		Assertions.assertTrue(dealer.getHand().getCards().size() > PREVIOUS_DEALER_HAND_SIZE);
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
		catch (AssertionError e)
		{
			System.out.println(e.getMessage());
		}
	}

	private void _handTest(int expectedSize)
	{
		Assertions.assertTrue(expectedSize >= 0);
		assertEquals(expectedSize, dealerHand.getCards().size());
		assertEquals(expectedSize, mainHand.getCards().size());
		assertEquals(dealerHand, mainHand);
		assertEquals(dealerHand.toString(), mainHand.toString());
		assertEquals(dealerHand.toStringPretty(), mainHand.toStringPretty());
	}

	private void handTest()
	{
		_handTest(0);
		mainHand.addCards(shoe.draw());
		dealerHand.addCards(shoe.draw());
		mainHand.removeCard(0);
		dealerHand.removeCard(0);
		final var newCard = shoe.draw();
		dealerHand.addCards(newCard);
		mainHand.addCards(newCard);
		_handTest(1);

		Assertions.assertTrue(blackjack.isBlackjack());
		Assertions.assertTrue(pocketPair.isPocketPair());

		final var bustHand = new Hand();
		while (bustHand.getScore() <= StandardRuleConfig.TOP_SCORE)
		{
			bustHand.addCards(shoe.draw());
		}

		Assertions.assertTrue(bustHand.isBusted());
	}

	@BeforeEach
	void init() throws InsufficientChipsException
	{
		blackjack = randomBlackjack();
		dealer = new Dealer();
		dealerHand = new Hand();
		mainContext = new HandContext(BET, HandContextType.MAIN);
		player1 = new Player();
		player2 = new Player();
		mainHand = new Hand();
		pocketPair = randomPocketPair();
		shoe = new Shoe(90, 8);
		splitContext = new HandContext(BET, HandContextType.SPLIT);
	}

	@RepeatedTest(TEST_ITERATIONS)
	void main() throws InsufficientChipsException
	{
		handTest();
		dealerTest();
		handContextTest();
		playerTest();
	}

	private void playerTest() throws InsufficientChipsException
	{
		assertEquals(player1.getChips(), player2.getChips());
		assertEquals(player1.toString(), player1.toString());
		player1.addContext(mainContext);
		player1.setChips(PLAYER_INITIAL_CHIP_AMOUNT);
		player2.addContext(splitContext);
		player2.setChips(PLAYER_INITIAL_CHIP_AMOUNT);
		assertTrue(PLAYER_INITIAL_CHIP_AMOUNT.compareTo(player1.getChips()) == 0
			&& PLAYER_INITIAL_CHIP_AMOUNT.compareTo(player2.getChips()) == 0
		);
		assertEquals(player1.getContexts().size(), player2.getContexts().size());
		player1.clearContexts();
		player2.removeContext(0);
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

		BLACKJACK_HAND.addCards(ACE);
		BLACKJACK_HAND.addCards(new Card(
			Rank.values()[RANDOM_RANK_INDEX],
			randomSuit()
		));

		return BLACKJACK_HAND;
	}

	private Hand randomPocketPair()
	{
		final var POCKET_PAIR = new Hand();
		final int RANDOM_RANK_INDEX = (int) (Math.random() * Rank.values().length);

		POCKET_PAIR.addCards(new Card(
			Rank.values()[RANDOM_RANK_INDEX],
			Suit.values()[0]
		));
		POCKET_PAIR.addCards(new Card(
			Rank.values()[RANDOM_RANK_INDEX],
			Suit.values()[1]
		));

		return POCKET_PAIR;
	}

	private Suit randomSuit()
	{
		return Suit.values()[(int) (Math.random() * Suit.values().length)];
	}
}