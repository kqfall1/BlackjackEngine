package com.github.kqfall1.java.blackjackEngine.showdown;

import com.github.kqfall1.java.blackjackEngine.engine.CustomDeckTest;
import com.github.kqfall1.java.blackjackEngine.model.engine.BlackjackConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class ShowdownNormalTest extends CustomDeckTest
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
		final var initialChipAmount = engine.getPlayer().getChips();
		advanceThroughDealerTurn(initialChipAmount);
		final var chipAmountAfterBetting = engine.getPlayer().getChips();
		final var potAmount = engine.getActiveHandContext().getPot().getAmount();

		engine.advanceAfterDealerTurn();
		engine.showdown();

		if (showdownMethodIndex < SHOWDOWN_NORMAL_DEALER_WIN_METHOD_COUNT)
		{
			Assertions.assertTrue(engine.getDealer().getHand().getScore() > engine.getActiveHandContext().getHand().getScore());
			Assertions.assertTrue(nearlyEquals(
				chipAmountAfterBetting.stripTrailingZeros(),
				engine.getPlayer().getChips(),
				BlackjackConstants.DEFAULT_CHIP_SCALE
			));

			Assertions.assertEquals(chipAmountAfterBetting, engine.getPlayer().getChips());
		}
		else if (showdownMethodIndex < SHOWDOWN_NORMAL_DEALER_WIN_METHOD_COUNT + SHOWDOWN_NORMAL_PLAYER_WIN_METHOD_COUNT)
		{
			Assertions.assertTrue(engine.getDealer().getHand().getScore() < engine.getActiveHandContext().getHand().getScore());
			engine.advanceAfterShowdown();

			Assertions.assertTrue(nearlyEquals(
				chipAmountAfterBetting.add(potAmount).stripTrailingZeros(),
				engine.getPlayer().getChips(),
				BlackjackConstants.DEFAULT_CHIP_SCALE
			));
		}
		else
		{
			Assertions.assertEquals(engine.getDealer().getHand().getScore(), engine.getActiveHandContext().getHand().getScore());
			Assertions.assertTrue(nearlyEquals(initialChipAmount, engine.getPlayer().getChips(), BlackjackConstants.DEFAULT_CHIP_SCALE));
		}

		advanceToEndOfRoundAfterShowdown();
	}
}