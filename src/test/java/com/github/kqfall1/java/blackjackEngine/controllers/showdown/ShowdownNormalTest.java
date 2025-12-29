package com.github.kqfall1.java.blackjackEngine.controllers.showdown;

import com.github.kqfall1.java.blackjackEngine.controllers.CustomDeckTest;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class ShowdownNormalTest extends CustomDeckTest
{
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/ShowdownTest.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.playerAction.ShowdownTest.log";
	private static final int SHOWDOWN_DEALER_WIN_METHOD_COUNT = 2;
	private static final int SHOWDOWN_PLAYER_WIN_METHOD_COUNT = 2;
	private static final int SHOWDOWN_METHOD_COUNT = 6;
	private static int showdownMethodIndex;

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
		final var PREVIOUS_CHIP_AMOUNT = super.engine.getPlayer().getChips();
		super.advanceToDealerTurn(PREVIOUS_CHIP_AMOUNT);
		super.engine.advanceAfterDealerTurn();

		if (showdownMethodIndex < SHOWDOWN_DEALER_WIN_METHOD_COUNT)
		{
			Assertions.assertTrue(
				super.engine.getDealer().getHand().getScore()
					> super.engine.getActiveHandContext().getHand().getScore()
				&& PREVIOUS_CHIP_AMOUNT.compareTo(super.engine.getPlayer().getChips()) > 0
			);
		}
		else if (showdownMethodIndex <
			SHOWDOWN_DEALER_WIN_METHOD_COUNT + SHOWDOWN_PLAYER_WIN_METHOD_COUNT)
		{
			Assertions.assertTrue(
				super.engine.getDealer().getHand().getScore()
					< super.engine.getActiveHandContext().getHand().getScore()
				&& PREVIOUS_CHIP_AMOUNT.compareTo(super.engine.getPlayer().getChips()) < 0
			);
		}
		else
		{
			Assertions.assertTrue(
				super.engine.getDealer().getHand().getScore()
					== super.engine.getActiveHandContext().getHand().getScore()
				&& PREVIOUS_CHIP_AMOUNT.compareTo(super.engine.getPlayer().getChips()) == 0
			);
		}

		super.engine.advanceAfterShowdown();
		super.engine.advanceAfterReset();
	}
}