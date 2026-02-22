package com.github.kqfall1.java.blackjackEngine.showdown;

import com.github.kqfall1.java.blackjackEngine.engine.CustomDeckTest;
import com.github.kqfall1.java.blackjackEngine.model.engine.BlackjackConstants;
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
			Assertions.assertTrue(
				nearlyEquals(
					CHIP_AMOUNT_AFTER_BETTING.stripTrailingZeros(),
					super.engine.getPlayer().getChips(),
					BlackjackConstants.DEFAULT_CHIP_SCALE
				)
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

			Assertions.assertTrue(
				nearlyEquals(
					CHIP_AMOUNT_AFTER_BETTING
						.add(POT_AMOUNT)
						.stripTrailingZeros(),
					super.engine.getPlayer().getChips(),
					BlackjackConstants.DEFAULT_CHIP_SCALE
				)
			);
		}
		else
		{
			Assertions.assertEquals(
				super.engine.getDealer().getHand().getScore(),
				super.engine.getActiveHandContext().getHand().getScore()
			);
			Assertions.assertTrue(
				nearlyEquals(
					INITIAL_CHIP_AMOUNT,
					super.engine.getPlayer().getChips(),
					BlackjackConstants.DEFAULT_CHIP_SCALE
				)
			);

			super.engine.advanceAfterShowdown();
		}

		super.engine.advanceAfterReset();
	}
}