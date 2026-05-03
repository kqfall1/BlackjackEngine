package com.github.kqfall1.java.blackjackEngine.rules.splitting;

import com.github.kqfall1.java.blackjackEngine.engine.CustomDeckTest;
import com.github.kqfall1.java.blackjackEngine.model.enums.Rank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class SplitAndHitTest extends CustomDeckTest
{
	@BeforeEach
	@Override
	public void init()
	{
		initCardsForSplittingAndHittingOnce(Rank.ACE);
		initDependencies();
		ruleset.getConfig().setMaximumSplitCount(MAXIMUM_SPLIT_COUNT);
		ruleset.getConfig().setSplittingAcesAllowed(true);
		initEngine();
		engine.getDealer().setCardSource(testDeck);
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	public void main()
	{
		advanceToPlayerTurn(getSplitDownTestMaximumInitialBetAmount());
		initSplitHands(() ->
		{
			final var activeHandContextIndex = engine.getActiveHandContextIndex();
			engine.playerHit();
			if (activeHandContextIndex == engine.getActiveHandContextIndex())
			{
				engine.playerStand();
			}
		});
		advanceThroughShowdownsAfterPlayerTurn();
		advanceToEndOfRoundAfterShowdown();
	}
}