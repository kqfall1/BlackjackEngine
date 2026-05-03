package com.github.kqfall1.java.blackjackEngine.dealer;

import com.github.kqfall1.java.blackjackEngine.engine.CustomDeckTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

public class DealerHitAndBustTest extends CustomDeckTest
{
	@BeforeEach
	@Override
	public void init()
	{
		_initCardsForBust();
		initDependencies();
		initEngine();
		engine.getDealer().setCardSource(testDeck);
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	public void main()
	{
		advanceThroughDealerTurn(engine.getPlayer().getChips().subtract(engine.getRuleset().getConfig().getMinimumBetAmount()));
		Assertions.assertTrue(ruleset.isHandBusted(engine.getDealer().getHand()));
		engine.advanceAfterDealerTurn();
		engine.showdown();
		advanceToEndOfRoundAfterShowdown();
	}
}