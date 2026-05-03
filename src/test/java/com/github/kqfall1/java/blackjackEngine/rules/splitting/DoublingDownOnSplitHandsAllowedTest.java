package com.github.kqfall1.java.blackjackEngine.rules.splitting;

import com.github.kqfall1.java.blackjackEngine.engine.CustomDeckTest;
import com.github.kqfall1.java.blackjackEngine.model.enums.Rank;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class DoublingDownOnSplitHandsAllowedTest extends CustomDeckTest
{
	@BeforeEach
	@Override
	public void init()
	{
		initCardsForSplittingAndHittingOnce(Rank.TEN);
		initDependencies();
		ruleset.getConfig().setMaximumSplitCount(MAXIMUM_SPLIT_COUNT);
		ruleset.getConfig().setDoublingDownOnSplitHandsAllowed(true);
		initEngine();
		engine.getDealer().setCardSource(testDeck);
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	public void main()
	{
		Assertions.assertTrue(ruleset.getConfig().isDoublingDownOnSplitHandsAllowed());
		advanceToPlayerTurn(DOUBLE_DOWN_TEST_MAXIMUM_INITIAL_BET_AMOUNT);
		initSplitHands(engine::playerDoubleDown);
		advanceThroughShowdownsAfterPlayerTurn();
		advanceToEndOfRoundAfterShowdown();
	}
}