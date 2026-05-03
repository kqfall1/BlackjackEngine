package com.github.kqfall1.java.blackjackEngine.dealer;

import com.github.kqfall1.java.blackjackEngine.engine.CustomDeckTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class DealerHitAndStandTest extends CustomDeckTest
{
	private int showdownMethodIndex;

	@BeforeEach
	@Override
	public void init()
	{
		showdownMethodIndex = _initCardsForNormalShowdown();
		initDependencies();
		initEngine();
		engine.getDealer().setCardSource(testDeck);
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	public void main()
	{
		advanceThroughDealerTurn(engine.getPlayer().getChips().subtract(engine.getRuleset().getConfig().getMinimumBetAmount()));

		if (showdownMethodIndex < SHOWDOWN_NORMAL_DEALER_WIN_METHOD_COUNT)
		{
			Assertions.assertTrue(
				engine.getActiveHandContext().getHand().getScore()
				< engine.getDealer().getHand().getScore()
			);
		}
		else if (showdownMethodIndex < SHOWDOWN_NORMAL_DEALER_WIN_METHOD_COUNT + SHOWDOWN_NORMAL_PLAYER_WIN_METHOD_COUNT)
		{
			Assertions.assertTrue(
				engine.getActiveHandContext().getHand().getScore()
				> engine.getDealer().getHand().getScore()
			);
		}
		else
		{
			Assertions.assertEquals(
				engine.getActiveHandContext().getHand().getScore(),
				engine.getDealer().getHand().getScore()
			);
		}

		engine.advanceAfterDealerTurn();
		engine.showdown();
		advanceToEndOfRoundAfterShowdown();
	}
}