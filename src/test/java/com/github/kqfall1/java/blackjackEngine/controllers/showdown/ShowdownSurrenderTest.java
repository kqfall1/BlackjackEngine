package com.github.kqfall1.java.blackjackEngine.controllers.showdown;

import com.github.kqfall1.java.blackjackEngine.controllers.CustomDeckTest;
import com.github.kqfall1.java.blackjackEngine.model.engine.BlackjackConstants;
import com.github.kqfall1.java.blackjackEngine.model.engine.EngineState;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class ShowdownSurrenderTest extends CustomDeckTest
{
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/ShowdownSurrenderTest.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.playerAction.ShowdownSurrenderTest.log";

	@BeforeEach
	@Override
	public void init() throws InsufficientChipsException, IOException
	{
		super._initCardsForBust();
		super.initDependencies();
		super.config.setSurrenderingAllowed(true);
		super.initEngine(LOG_FILE_PATH, LOGGER_NAME);
		super.engine.getDealer().setCardSource(testDeck);
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	public void main() throws Exception
	{
		final var PREVIOUS_CHIP_AMOUNT = super.engine.getPlayer().getChips();
		final var BET_AMOUNT = super.advanceToPlayerTurn(PREVIOUS_CHIP_AMOUNT);
		final var CHIP_AMOUNT_AFTER_BETTING = super.engine.getPlayer().getChips();

		if (super.engine.getState() == EngineState.PLAYER_TURN)
		{
			super.engine.playerSurrender();

			Assertions.assertTrue(
				nearlyEquals(
					CHIP_AMOUNT_AFTER_BETTING,
					super.engine.getPlayer().getChips(),
					BlackjackConstants.DEFAULT_CHIP_SCALE
				)
			);
			Assertions.assertTrue(super.engine.getActiveHandContext().hasSurrendered());

			super.engine.advanceAfterPlayerTurn();
		}

		super.engine.advanceAfterShowdown();

		Assertions.assertTrue(
			nearlyEquals(
				CHIP_AMOUNT_AFTER_BETTING
					.add(BET_AMOUNT.divide(BigDecimal.TWO, MathContext.DECIMAL128))
					.stripTrailingZeros(),
				super.engine.getPlayer().getChips(),
				BlackjackConstants.DEFAULT_CHIP_SCALE
			)
		);

		super.engine.advanceAfterReset();
	}
}