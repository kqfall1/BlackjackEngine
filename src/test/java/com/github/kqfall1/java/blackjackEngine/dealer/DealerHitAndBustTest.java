package com.github.kqfall1.java.blackjackEngine.dealer;

import com.github.kqfall1.java.blackjackEngine.engine.CustomDeckTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

public class DealerHitAndBustTest extends CustomDeckTest
{
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/DealerHitAndBustTest.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.dealer.DealerHitAndBustTest.log";

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
		super.advanceToDealerTurn(super.engine.getPlayer().getChips());
		Assertions.assertTrue(
			super.ruleset.isHandBusted(super.engine.getDealer().getHand())
		);
		super.advanceToEndOfRound();
	}
}