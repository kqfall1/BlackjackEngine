package com.github.kqfall1.java.blackjackEngine.controllers.showdown;

import com.github.kqfall1.java.blackjackEngine.controllers.CustomDeckTest;
import com.github.kqfall1.java.blackjackEngine.model.engine.StandardRuleConfig;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import java.io.IOException;

public class ShowdownBlackjackTest extends CustomDeckTest
{
	private int blackjackMethodIndex;
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/ShowdownNormalTest.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.playerAction.ShowdownTest.log";

	@BeforeEach
	@Override
	public void init() throws InsufficientChipsException, IOException
	{
		blackjackMethodIndex = super._initCardsForBlackjack();
		super.initDependencies();
		super.initEngine(LOG_FILE_PATH, LOGGER_NAME);
		super.engine.getDealer().setDeck(testDeck);
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	public void main() throws Exception
	{
		final var INITIAL_CHIP_AMOUNT = super.engine.getPlayer().getChips();
		super.advanceToPlayerTurn(INITIAL_CHIP_AMOUNT);
		final var CHIP_AMOUNT_AFTER_BETTING = super.engine.getPlayer().getChips();
		final var POT_AMOUNT = super.engine.getActiveHandContext().getPot().getAmount();

		if (blackjackMethodIndex < SHOWDOWN_BLACKJACK_DEALER_METHOD_COUNT)
		{
			assertEquals(
				StandardRuleConfig.TOP_SCORE,
				super.engine.getDealer().getHand().getScore()
			);
			assertTrue(
				super.engine.getActiveHandContext().getHand().getScore()
					< StandardRuleConfig.TOP_SCORE
			);
			assertEquals(
				CHIP_AMOUNT_AFTER_BETTING,
				super.engine.getPlayer().getChips()
			);
		}
		else if (blackjackMethodIndex
			< SHOWDOWN_BLACKJACK_DEALER_METHOD_COUNT + SHOWDOWN_BLACKJACK_PLAYER_METHOD_COUNT)
		{
			assertEquals(
				StandardRuleConfig.TOP_SCORE,
				super.engine.getActiveHandContext().getHand().getScore()
			);
			assertTrue(
				super.engine.getDealer().getHand().getScore()
					< StandardRuleConfig.TOP_SCORE
			);
			assertEquals(
				CHIP_AMOUNT_AFTER_BETTING.add(
					POT_AMOUNT.multiply(
						StandardRuleConfig.INSURANCE.getPayoutMultiplier()
					)
				),
				super.engine.getPlayer().getChips()
			);
		}
		else
		{
			assertTrue(
				super.engine.getActiveHandContext().getHand().getScore()
					== super.engine.getDealer().getHand().getScore()
				&& super.engine.getDealer().getHand().getScore()
					== StandardRuleConfig.TOP_SCORE
			);
			assertEquals(
				INITIAL_CHIP_AMOUNT,
				super.engine.getPlayer().getChips()
			);
		}

		super.engine.advanceAfterShowdown();
		super.engine.advanceAfterReset();
	}
}