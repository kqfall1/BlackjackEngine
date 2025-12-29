package com.github.kqfall1.java.blackjackEngine.controllers.dealer;

import com.github.kqfall1.java.blackjackEngine.controllers.CustomDeckTest;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

public class DealerHitAndBustTest extends CustomDeckTest
{
	private static final int BUST_METHOD_COUNT = 3;
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/DealerHitAndBustTest.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.dealer.DealerHitAndBustTest.log";

	@BeforeEach
	@Override
	public void init() throws InsufficientChipsException, IOException
	{
		final var BUST_METHOD_INDEX = (int) (Math.random() * BUST_METHOD_COUNT);
		switch (BUST_METHOD_INDEX)
		{
			case 0 -> super.initCardsForBust1();
			case 1 -> super.initCardsForBust2();
			case 2 -> super.initCardsForBust3();
		}

		super.initDependencies();
		super.initEngine(LOG_FILE_PATH, LOGGER_NAME);
		super.engine.getDealer().setDeck(testDeck);
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	public void main() throws Exception
	{
		super.advanceToDealerTurn(super.engine.getPlayer().getChips());
		Assertions.assertTrue(super.engine.getDealer().getHand().isBusted());
		super.advanceToEndOfRoundAfterPotentialDealerTurn();
	}
}