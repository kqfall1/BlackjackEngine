package com.github.kqfall1.java.blackjackEngine.insurance;

import com.github.kqfall1.java.blackjackEngine.engine.CustomDeckTest;
import com.github.kqfall1.java.blackjackEngine.model.enums.Rank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class InsuranceBetDeclineAndSplitTest extends CustomDeckTest
{
	@BeforeEach
	@Override
	public void init()
	{
		initCardsForInsuranceAndSplitting(Rank.JACK);
		initDependencies();
		ruleset.getConfig().setMaximumSplitCount(MAXIMUM_SPLIT_COUNT);
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