package com.github.kqfall1.java.blackjackEngine.controllers.dealer;

import com.github.kqfall1.java.blackjackEngine.controllers.CustomDeckTest;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class DealerHitAndStandTest extends CustomDeckTest
{
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/DealerHitAndStandTest.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.dealer.DealerHitAndStandTest.log";
	private static final int SHOWDOWN_DEALER_WIN_METHOD_COUNT = 2;
	private static final int SHOWDOWN_PLAYER_WIN_METHOD_COUNT = 2;
	private static final int SHOWDOWN_METHOD_COUNT = 6;
	private int showdownMethodIndex;

	@BeforeEach
	@Override
	public void init() throws InsufficientChipsException, IOException
	{
		showdownMethodIndex = (int) (Math.random() * SHOWDOWN_METHOD_COUNT);

		switch (showdownMethodIndex)
		{
			case 0 -> super.initCardsForDealerWin1();
			case 1 -> super.initCardsForDealerWin2();
			case 2 -> super.initCardsForPlayerWin1();
			case 3 -> super.initCardsForPlayerWin2();
			case 4 -> super.initCardsForPush1();
			case 5 -> super.initCardsForPush2();
		}

		super.initDependencies();
		super.initEngine(LOG_FILE_PATH, LOGGER_NAME);
		super.engine.getDealer().setDeck(testDeck);
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	public void main() throws Exception
	{
		super.advanceToDealerTurn(super.engine.getPlayer().getChips());

		if (showdownMethodIndex < SHOWDOWN_DEALER_WIN_METHOD_COUNT)
		{
			Assertions.assertTrue(
				super.engine.getActiveHandContext().getHand().getScore()
				< super.engine.getDealer().getHand().getScore()
			);
		}
		else if (showdownMethodIndex < SHOWDOWN_DEALER_WIN_METHOD_COUNT + SHOWDOWN_PLAYER_WIN_METHOD_COUNT)
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

		super.advanceToEndAfterPotentialDealerTurn();
	}
}