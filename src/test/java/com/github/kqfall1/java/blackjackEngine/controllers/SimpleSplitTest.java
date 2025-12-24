package com.github.kqfall1.java.blackjackEngine.controllers;

import com.github.kqfall1.java.blackjackEngine.model.cards.TestDeck;
import com.github.kqfall1.java.blackjackEngine.model.engine.EngineState;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * To test the {@code BlackjackEngine} splitting mechanism in isolation.
 *
 * @author kqfall1
 * @since 23/12/2025
 */
final class SimpleSplitTest extends SplitTest
{
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/SimpleSplitTest.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.SimpleSplitTest.log";

	@BeforeEach
	@Override
	void init() throws InsufficientChipsException, IOException
	{
		super.initCardsForPocket7s();
		super.init();
		engine = new BlackjackEngine(config, LISTENER, LOG_FILE_PATH, LOGGER_NAME);
		super.start();
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	void main() throws Exception
	{
		final var PREVIOUS_CHIP_AMOUNT = engine.getPlayer().getChips();
		engine.placeHandBet(DEFAULT_BET_AMOUNT);
		deal();

		if (engine.getState() == EngineState.PLAYER_TURN)
		{
			Assertions.assertEquals(
				PREVIOUS_CHIP_AMOUNT.subtract(DEFAULT_BET_AMOUNT),
				engine.getPlayer().getChips()
			);
			Assertions.assertFalse(engine.getActiveHandContext().isAltered());
			engine.playerSplit();

			Assertions.assertEquals(
				PREVIOUS_CHIP_AMOUNT.subtract(DEFAULT_BET_AMOUNT.multiply(BigDecimal.TWO)),
				engine.getPlayer().getChips()
			);
			engine.playerStand();
			engine.playerStand();
		}
	}
}