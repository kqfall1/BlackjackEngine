package com.github.kqfall1.java.blackjackEngine.showdown;

import com.github.kqfall1.java.blackjackEngine.engine.CustomDeckTest;
import com.github.kqfall1.java.blackjackEngine.model.engine.BlackjackConstants;

import java.math.BigDecimal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class ShowdownPlayerBustTest extends CustomDeckTest
{
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/ShowdownPlayerBustTest.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.playerAction.ShowdownPlayerBustTest.log";

	@BeforeEach
	@Override
	public void init() {
		super._initCardsForBust();
		super.initDependencies();
		super.initEngine(LOG_FILE_PATH, LOGGER_NAME);
		super.engine.getDealer().setCardSource(testDeck);
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	public void main()
	{
		BigDecimal chipAmountAfterBetting;

		super.advanceToPlayerTurn(super.engine.getPlayer().getChips());
		chipAmountAfterBetting = super.engine.getPlayer().getChips();
		super.engine.playerHit();
		Assertions.assertTrue(
			super.ruleset.isHandBusted(super.engine.getActiveHandContext().getHand())
		);
		super.engine.showdown();
		super.engine.advanceAfterShowdown();

		Assertions.assertTrue(
			nearlyEquals(
				chipAmountAfterBetting,
				super.engine.getPlayer().getChips(),
				BlackjackConstants.DEFAULT_CHIP_SCALE
			)
		);

		super.engine.advanceAfterReset();
	}
}