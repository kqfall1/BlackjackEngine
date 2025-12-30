package com.github.kqfall1.java.blackjackEngine.controllers.showdown;

import com.github.kqfall1.java.blackjackEngine.controllers.CustomDeckTest;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class ShowdownNormalTest extends CustomDeckTest
{
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/ShowdownNormalTest.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.playerAction.ShowdownNormalTest.log";
	private int showdownMethodIndex;

	@BeforeEach
	@Override
	public void init() throws InsufficientChipsException, IOException
	{
		showdownMethodIndex = super._initCardsForNormalShowdown();
		super.initDependencies();
		super.initEngine(LOG_FILE_PATH, LOGGER_NAME);
		super.engine.getDealer().setDeck(testDeck);
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	public void main() throws Exception
	{
		final var INITIAL_CHIP_AMOUNT = super.engine.getPlayer().getChips();
		super.advanceToDealerTurn(INITIAL_CHIP_AMOUNT);
		final var CHIP_AMOUNT_AFTER_BETTING = super.engine.getPlayer().getChips();
		final var POT_AMOUNT = super.engine.getActiveHandContext().getPot().getAmount();

		super.engine.advanceAfterDealerTurn();

		if (showdownMethodIndex < SHOWDOWN_NORMAL_DEALER_WIN_METHOD_COUNT)
		{
			Assertions.assertTrue(
				super.engine.getDealer().getHand().getScore()
					> super.engine.getActiveHandContext().getHand().getScore()
			);
			Assertions.assertEquals(
				CHIP_AMOUNT_AFTER_BETTING.stripTrailingZeros(),
				super.engine.getPlayer().getChips()
			);

			super.engine.advanceAfterShowdown();

			Assertions.assertEquals(
				CHIP_AMOUNT_AFTER_BETTING,
				super.engine.getPlayer().getChips()
			);
		}
		else if (showdownMethodIndex <
			SHOWDOWN_NORMAL_DEALER_WIN_METHOD_COUNT + SHOWDOWN_NORMAL_PLAYER_WIN_METHOD_COUNT)
		{
			Assertions.assertTrue(
				super.engine.getDealer().getHand().getScore()
					< super.engine.getActiveHandContext().getHand().getScore()
			);

			super.engine.advanceAfterShowdown();

			Assertions.assertEquals(
				CHIP_AMOUNT_AFTER_BETTING
					.add(POT_AMOUNT)
					.stripTrailingZeros(),
				super.engine.getPlayer().getChips()
			);
		}
		else
		{
			Assertions.assertEquals(
				super.engine.getDealer().getHand().getScore(),
				super.engine.getActiveHandContext().getHand().getScore()
			);
			Assertions.assertEquals(
				INITIAL_CHIP_AMOUNT,
				super.engine.getPlayer().getChips()
			);

			super.engine.advanceAfterShowdown();
		}

		super.engine.advanceAfterReset();
	}
}