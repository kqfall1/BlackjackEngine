package com.github.kqfall1.java.blackjackEngine.showdown;

import com.github.kqfall1.java.blackjackEngine.engine.CustomDeckTest;
import com.github.kqfall1.java.blackjackEngine.model.engine.BlackjackConstants;
import com.github.kqfall1.java.blackjackEngine.model.enums.BlackjackEngineState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ShowdownBlackjackTest extends CustomDeckTest
{
	private int blackjackMethodIndex;

	@BeforeEach
	@Override
	public void init()
	{
		blackjackMethodIndex = _initCardsForBlackjack();
		initDependencies();
		initEngine();
		engine.getDealer().setCardSource(testDeck);
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	public void main()
	{
		final var initialChipAmount = engine.getPlayer().getChips();
		advanceThroughDealerTurn(initialChipAmount.subtract(engine.getRuleset().getConfig().getMinimumBetAmount()));
		final var chipAmountAfterBetting = engine.getPlayer().getChips();
		final var potAmount = engine.getActiveHandContext().getPot().getAmount();

		if (engine.getState() == BlackjackEngineState.DEALER_TURN)
		{
			engine.advanceAfterPlayerTurn();
		}

		engine.showdown();

		if (blackjackMethodIndex < SHOWDOWN_BLACKJACK_DEALER_METHOD_COUNT)
		{
			assertEquals(BlackjackConstants.TOP_SCORE, engine.getDealer().getHand().getScore());
			assertTrue(engine.getActiveHandContext().getHand().getScore() < BlackjackConstants.TOP_SCORE);
			assertTrue(nearlyEquals(chipAmountAfterBetting, engine.getPlayer().getChips(), BlackjackConstants.DEFAULT_CHIP_SCALE));
		}
		else if (blackjackMethodIndex < SHOWDOWN_BLACKJACK_DEALER_METHOD_COUNT + SHOWDOWN_BLACKJACK_PLAYER_METHOD_COUNT)
		{
			assertEquals(BlackjackConstants.TOP_SCORE, engine.getActiveHandContext().getHand().getScore());
			assertTrue(engine.getDealer().getHand().getScore() < BlackjackConstants.TOP_SCORE);
			assertTrue(nearlyEquals(
				chipAmountAfterBetting.add(potAmount.multiply(BlackjackConstants.BLACKJACK_RATIO.getPayoutMultiplier())),
				engine.getPlayer().getChips(),
				BlackjackConstants.DEFAULT_CHIP_SCALE
			));
		}
		else
		{
			assertTrue(
				engine.getActiveHandContext().getHand().getScore()
					== engine.getDealer().getHand().getScore()
				&& engine.getDealer().getHand().getScore()
					== BlackjackConstants.TOP_SCORE
			);
			assertTrue(nearlyEquals(initialChipAmount, engine.getPlayer().getChips(), BlackjackConstants.DEFAULT_CHIP_SCALE));
		}

		advanceToEndOfRoundAfterShowdown();
	}
}