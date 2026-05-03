package com.github.kqfall1.java.blackjackEngine.showdown;

import com.github.kqfall1.java.blackjackEngine.engine.CustomDeckTest;
import com.github.kqfall1.java.blackjackEngine.model.engine.BlackjackConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class ShowdownPlayerBustTest extends CustomDeckTest
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
		advanceToPlayerTurn(engine.getPlayer().getChips().subtract(engine.getRuleset().getConfig().getMinimumBetAmount()));
		final var chipAmountAfterBetting = engine.getPlayer().getChips();
		engine.playerHit();
		Assertions.assertTrue(ruleset.isHandBusted(engine.getActiveHandContext().getHand()));
		engine.showdown();
		advanceToEndOfRoundAfterShowdown();
		Assertions.assertTrue(nearlyEquals(chipAmountAfterBetting, engine.getPlayer().getChips(), BlackjackConstants.DEFAULT_CHIP_SCALE));
	}
}