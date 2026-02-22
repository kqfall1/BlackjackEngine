package com.github.kqfall1.java.blackjackEngine.showdown;

import com.github.kqfall1.java.blackjackEngine.engine.CustomDeckTest;
import com.github.kqfall1.java.blackjackEngine.model.engine.BlackjackConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

public class ShowdownBlackjackTest extends CustomDeckTest
{
	private int blackjackMethodIndex;
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/ShowdownNormalTest.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.playerAction.ShowdownTest.log";

	@BeforeEach
	@Override
	public void init() {
		blackjackMethodIndex = super._initCardsForBlackjack();
		super.initDependencies();
		super.initEngine(LOG_FILE_PATH, LOGGER_NAME);
		super.engine.getDealer().setCardSource(testDeck);
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	public void main()
	{
		final var INITIAL_CHIP_AMOUNT = super.engine.getPlayer().getChips();
		super.advanceToDealerTurn(INITIAL_CHIP_AMOUNT);
		final var CHIP_AMOUNT_AFTER_BETTING = super.engine.getPlayer().getChips();
		final var POT_AMOUNT = super.engine.getActiveHandContext().getPot().getAmount();

		super.engine.showdown();

		if (blackjackMethodIndex < SHOWDOWN_BLACKJACK_DEALER_METHOD_COUNT)
		{
			assertEquals(
				BlackjackConstants.DEFAULT_TOP_SCORE,
				super.engine.getDealer().getHand().getScore()
			);
			assertTrue(
				super.engine.getActiveHandContext().getHand().getScore()
					< BlackjackConstants.DEFAULT_TOP_SCORE
			);
			assertTrue(
				nearlyEquals(
					CHIP_AMOUNT_AFTER_BETTING,
					super.engine.getPlayer().getChips(),
					BlackjackConstants.DEFAULT_CHIP_SCALE
				)
			);
		}
		else if (blackjackMethodIndex
			< SHOWDOWN_BLACKJACK_DEALER_METHOD_COUNT + SHOWDOWN_BLACKJACK_PLAYER_METHOD_COUNT)
		{
			assertEquals(
				BlackjackConstants.DEFAULT_TOP_SCORE,
				super.engine.getActiveHandContext().getHand().getScore()
			);
			assertTrue(
				super.engine.getDealer().getHand().getScore()
					< BlackjackConstants.DEFAULT_TOP_SCORE
			);
			assertTrue(
				nearlyEquals(
					CHIP_AMOUNT_AFTER_BETTING.add(
						POT_AMOUNT.multiply(
							BlackjackConstants.BLACKJACK_RATIO.getPayoutMultiplier()
						)
					),
					super.engine.getPlayer().getChips(),
					BlackjackConstants.DEFAULT_CHIP_SCALE
				)
			);
		}
		else
		{
			assertTrue(
				super.engine.getActiveHandContext().getHand().getScore()
					== super.engine.getDealer().getHand().getScore()
				&& super.engine.getDealer().getHand().getScore()
					== BlackjackConstants.DEFAULT_TOP_SCORE
			);
			assertTrue(
				nearlyEquals(
					INITIAL_CHIP_AMOUNT,
					super.engine.getPlayer().getChips(),
					BlackjackConstants.DEFAULT_CHIP_SCALE
				)
			);
		}

		super.advanceToEndOfRound();
	}
}