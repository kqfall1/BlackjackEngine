package com.github.kqfall1.java.blackjackEngine.showdown;

import com.github.kqfall1.java.blackjackEngine.engine.CustomDeckTest;
import com.github.kqfall1.java.blackjackEngine.model.engine.BlackjackConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class ShowdownDealerBustTest extends CustomDeckTest
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
		advanceThroughDealerTurn(engine.getPlayer().getChips());
		final var chipAmountAfterBetting = engine.getPlayer().getChips();
		final var potAmount = engine.getActiveHandContext().getPot().getAmount();
		Assertions.assertTrue(ruleset.isHandBusted(engine.getDealer().getHand()));
		engine.advanceAfterDealerTurn();
		engine.showdown();
		advanceToEndOfRoundAfterShowdown();

		Assertions.assertTrue(nearlyEquals(
			chipAmountAfterBetting.add(potAmount).stripTrailingZeros(),
			engine.getPlayer().getChips(),
			BlackjackConstants.DEFAULT_CHIP_SCALE
		));
	}
}