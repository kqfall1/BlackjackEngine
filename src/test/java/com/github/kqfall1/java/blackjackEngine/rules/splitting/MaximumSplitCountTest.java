package com.github.kqfall1.java.blackjackEngine.rules.splitting;

import com.github.kqfall1.java.blackjackEngine.engine.CustomDeckTest;
import com.github.kqfall1.java.blackjackEngine.model.enums.Rank;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class MaximumSplitCountTest extends CustomDeckTest
{
	@BeforeEach
	@Override
	public void init()
	{
		initCardsForSplittingWithoutHitting(Rank.ACE);
		initDependencies();
		ruleset.getConfig().setMaximumSplitCount(ThreadLocalRandom.current().nextInt(MAXIMUM_SPLIT_COUNT + 1));
		ruleset.getConfig().setSplittingAcesAllowed(true);
		initEngine();
		engine.getDealer().setCardSource(testDeck);
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	public void main()
	{
		advanceToPlayerTurn(SPLIT_TEST_MAXIMUM_INITIAL_BET_AMOUNT);
		initSplitHands(engine::playerStand);
		advanceThroughShowdownsAfterPlayerTurn();
		advanceToEndOfRoundAfterShowdown();
	}
}