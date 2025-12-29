package com.github.kqfall1.java.blackjackEngine.controllers.showdown;

import com.github.kqfall1.java.blackjackEngine.controllers.CustomDeckTest;
import com.github.kqfall1.java.blackjackEngine.model.engine.EngineState;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class ShowdownBustTest extends CustomDeckTest
{
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/ShowdownBustTest.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.playerAction.ShowdownBustTest.log";

	@BeforeEach
	@Override
	public void init() throws InsufficientChipsException, IOException
	{
		super._initCardsForBust();
		super.initDependencies();
		super.initEngine(LOG_FILE_PATH, LOGGER_NAME);
		super.engine.getDealer().setDeck(testDeck);
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	public void main() throws Exception
	{
		super.advanceToPlayerTurn(super.engine.getPlayer().getChips());
		final var CHIP_AMOUNT_AFTER_BETTING = super.engine.getPlayer().getChips();

		if (super.engine.getState() == EngineState.PLAYER_TURN)
		{
			super.engine.playerHit();

			Assertions.assertTrue(super.engine.getActiveHandContext().getHand().isBusted());
			Assertions.assertEquals(
				CHIP_AMOUNT_AFTER_BETTING,
				super.engine.getPlayer().getChips()
			);

			super.engine.advanceAfterPlayerTurn();
		}

		super.engine.advanceAfterShowdown();

		Assertions.assertEquals(
			CHIP_AMOUNT_AFTER_BETTING,
			super.engine.getPlayer().getChips()
		);

		super.engine.advanceAfterReset();
	}
}