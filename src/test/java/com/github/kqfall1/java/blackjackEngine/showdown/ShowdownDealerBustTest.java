package com.github.kqfall1.java.blackjackEngine.showdown;

import com.github.kqfall1.java.blackjackEngine.engine.CustomDeckTest;
import com.github.kqfall1.java.blackjackEngine.model.engine.BlackjackConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class ShowdownDealerBustTest extends CustomDeckTest
{
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/ShowdownDealerBustTest.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.playerAction.ShowdownDealerBustTest.log";

	@BeforeEach
	@Override
	public void init()
	{
		super._initCardsForBust();
		super.initDependencies();
		super.initEngine(LOG_FILE_PATH, LOGGER_NAME);
		super.engine.getDealer().setCardSource(testDeck);
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	public void main()
	{
		super.advanceThroughDealerTurn(super.engine.getPlayer().getChips());
		final var chipAmountAfterBetting = super.engine.getPlayer().getChips();
		final var potAmount = super.engine.getActiveHandContext().getPot().getAmount();
		Assertions.assertTrue(super.ruleset.isHandBusted(super.engine.getDealer().getHand()));
		super.engine.advanceAfterDealerTurn();
		super.engine.showdown();
		super.advanceToEndOfRoundAfterShowdown();

		Assertions.assertTrue(
			nearlyEquals(
				chipAmountAfterBetting.add(potAmount).stripTrailingZeros(),
				super.engine.getPlayer().getChips(),
				BlackjackConstants.DEFAULT_CHIP_SCALE
			)
		);
	}
}