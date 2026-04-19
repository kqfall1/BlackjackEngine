package com.github.kqfall1.java.blackjackEngine.subsystems;

import com.github.kqfall1.java.blackjackEngine.model.betting.Bet;
import com.github.kqfall1.java.blackjackEngine.model.cards.Shoe;
import com.github.kqfall1.java.blackjackEngine.model.cards.*;
import com.github.kqfall1.java.blackjackEngine.model.engine.BlackjackConstants;
import com.github.kqfall1.java.blackjackEngine.model.engine.BlackjackRulesetConfiguration;
import com.github.kqfall1.java.blackjackEngine.model.engine.StandardBlackjackRuleset;
import com.github.kqfall1.java.blackjackEngine.model.entities.Dealer;
import com.github.kqfall1.java.blackjackEngine.model.entities.Player;
import com.github.kqfall1.java.blackjackEngine.model.enums.Rank;
import com.github.kqfall1.java.blackjackEngine.model.enums.Suit;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import com.github.kqfall1.java.blackjackEngine.model.hands.Hand;
import com.github.kqfall1.java.blackjackEngine.model.hands.HandContext;
import com.github.kqfall1.java.blackjackEngine.model.enums.HandContextType;
import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

public final class EntityAndHandSubsystemTest
{
	private final Bet bet = new Bet(BET_AND_POT_INITIAL_AMOUNT);
	private static final BigDecimal BET_AND_POT_INITIAL_AMOUNT = BigDecimal.valueOf(1000);
	private Dealer dealer;
	private Hand dealerHand;
	private HandContext mainContext;
	private Hand mainHand;
	private Player player1;
	private Player player2;
	private static final BigDecimal PLAYER_INITIAL_CHIP_AMOUNT = BigDecimal.valueOf(5000);
	private Hand pocketPair = new Hand();
	private Shoe shoe;
	public static final double SHOE_PENETRATION_RANGE = Shoe.MAXIMUM_PENETRATION - Shoe.MINIMUM_PENETRATION;
	private HandContext splitContext;
	private static final int TEST_ITERATIONS = 5000;

	private void dealerTest()
	{
		final var previousHand = dealer.getHand();
		final var previousSource = dealer.getCardSource();
		dealer.setHand(dealerHand);
		dealer.setCardSource(shoe);
		Assertions.assertNotEquals(previousHand, dealer.getHand());
		Assertions.assertNotEquals(previousSource, dealer.getCardSource());
		final int PREVIOUS_DEALER_HAND_SIZE = dealer.getHand().getCards().size();
		dealer.getHand().addCards(dealer.getCardSource().draw());
		Assertions.assertTrue(dealer.getHand().getCards().size() > PREVIOUS_DEALER_HAND_SIZE);
	}

	private void handContextTest()
	{
		assertEquals(mainContext, new HandContext(bet, HandContextType.MAIN));
		assertEquals(bet, mainContext.getBet());
		assertEquals(BigDecimal.ZERO, mainContext.getPot().getAmount());
		assertEquals(splitContext, new HandContext(bet, HandContextType.SPLIT));
		assertEquals(bet, splitContext.getBet());
		assertEquals(BigDecimal.ZERO, splitContext.getPot().getAmount());
		Assertions.assertFalse(mainContext.isAltered());
		Assertions.assertFalse(mainContext.isSurrendered());
		Assertions.assertFalse(splitContext.isAltered());
		Assertions.assertFalse(splitContext.isSurrendered());
		Assertions.assertNotEquals(mainContext.getType(), splitContext.getType());

		mainContext.getPot().addChips(BET_AND_POT_INITIAL_AMOUNT);
		splitContext.getPot().addChips(BET_AND_POT_INITIAL_AMOUNT);
		assertEquals(mainContext.getPot().getAmount(), splitContext.getPot().getAmount());
		mainContext.setBet(new Bet(BigDecimal.valueOf(100)));
		Assertions.assertNotEquals(mainContext.getBet(), splitContext.getBet());
		mainContext.setSurrendered();
		Assertions.assertTrue(mainContext.isSurrendered());
		Assertions.assertNotEquals(mainContext.isSurrendered(), splitContext.isSurrendered());

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
		Assertions.assertTrue(pocketPair.isPocketPair());
		final var bustHand = new Hand();

		while (bustHand.getScore() <= BlackjackConstants.TOP_SCORE)
		{
			bustHand.addCards(shoe.draw());
		}
	}

	@BeforeEach
	void init()
	{
		final var config = new BlackjackRulesetConfiguration();
		final var penetration = Math.random() * SHOE_PENETRATION_RANGE + Shoe.MINIMUM_PENETRATION;
		final var numberOfDecks = ThreadLocalRandom.current().nextInt(Shoe.MINIMUM_NUMBER_OF_DECKS, Shoe.MAXIMUM_NUMBER_OF_DECKS + 1);
		final var ruleset = new StandardBlackjackRuleset(config);

		dealer = new Dealer(ruleset.getIncludedRanks(), numberOfDecks, penetration);
		dealerHand = new Hand();
		mainContext = new HandContext(bet, HandContextType.MAIN);
		player1 = new Player();
		player2 = new Player();
		mainHand = new Hand();
		pocketPair = randomPocketPair();
		shoe = new Shoe(ruleset.getIncludedRanks(), 8, 90);
		splitContext = new HandContext(bet, HandContextType.SPLIT);
	}

	@RepeatedTest(TEST_ITERATIONS)
	void main()
	{
		handTest();
		dealerTest();
		handContextTest();
		playerTest();
	}

	private void playerTest()
	{
		assertEquals(player1.getChips(), player2.getChips());
		assertEquals(player1.toString(), player1.toString());
		player1.addContext(mainContext);
		player1.setChips(PLAYER_INITIAL_CHIP_AMOUNT);
		player2.addContext(splitContext);
		player2.setChips(PLAYER_INITIAL_CHIP_AMOUNT);
		assertTrue(PLAYER_INITIAL_CHIP_AMOUNT.compareTo(player1.getChips()) == 0 && PLAYER_INITIAL_CHIP_AMOUNT.compareTo(player2.getChips()) == 0);
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

	private Hand randomPocketPair()
	{
		final var pocketPair = new Hand();
		final int randomRankIndex = (int) (Math.random() * Rank.values().length);
		pocketPair.addCards(new Card(Rank.values()[randomRankIndex], Suit.values()[0]));
		pocketPair.addCards(new Card(Rank.values()[randomRankIndex], Suit.values()[1]));
		return pocketPair;
	}
}