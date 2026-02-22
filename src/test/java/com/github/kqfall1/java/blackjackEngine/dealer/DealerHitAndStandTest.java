package com.github.kqfall1.java.blackjackEngine.dealer;

import com.github.kqfall1.java.blackjackEngine.engine.CustomDeckTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class DealerHitAndStandTest extends CustomDeckTest
{
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/DealerHitAndStandTest.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.dealer.DealerHitAndStandTest.log";
	private int showdownMethodIndex;

	@BeforeEach
	@Override
	public void init() {
		showdownMethodIndex = super._initCardsForNormalShowdown();
		super.initDependencies();
		super.initEngine(LOG_FILE_PATH, LOGGER_NAME);
		super.engine.getDealer().setCardSource(testDeck);
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	public void main()
	{
		super.advanceToDealerTurn(super.engine.getPlayer().getChips());

		if (showdownMethodIndex < SHOWDOWN_NORMAL_DEALER_WIN_METHOD_COUNT)
		{
			Assertions.assertTrue(
				super.engine.getActiveHandContext().getHand().getScore()
				< super.engine.getDealer().getHand().getScore()
			);
		}
		else if (showdownMethodIndex < SHOWDOWN_NORMAL_DEALER_WIN_METHOD_COUNT + SHOWDOWN_NORMAL_PLAYER_WIN_METHOD_COUNT)
		{
			Assertions.assertTrue(
				super.engine.getActiveHandContext().getHand().getScore()
				> super.engine.getDealer().getHand().getScore()
			);
		}
		else
		{
			Assertions.assertEquals(
				super.engine.getActiveHandContext().getHand().getScore(),
				super.engine.getDealer().getHand().getScore()
			);
		}

		super.advanceToEndOfRound();
	}
}