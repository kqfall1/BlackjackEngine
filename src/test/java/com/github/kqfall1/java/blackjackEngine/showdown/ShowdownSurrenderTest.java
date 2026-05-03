package com.github.kqfall1.java.blackjackEngine.showdown;

import com.github.kqfall1.java.blackjackEngine.engine.CustomDeckTest;
import com.github.kqfall1.java.blackjackEngine.model.engine.BlackjackConstants;
import com.github.kqfall1.java.blackjackEngine.model.enums.BlackjackEngineState;
import java.math.BigDecimal;
import java.math.MathContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class ShowdownSurrenderTest extends CustomDeckTest
{
	@BeforeEach
	@Override
	public void init()
	{
		_initCardsForBust();
		initDependencies();
		ruleset.getConfig().setSurrenderingAllowed(true);
		initEngine();
		engine.getDealer().setCardSource(testDeck);
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	public void main()
	{
		final var previousChipAmount = engine.getPlayer().getChips();
		final var betAmount = advanceToPlayerTurn(previousChipAmount.subtract(engine.getRuleset().getConfig().getMinimumBetAmount()));
		final var chipAmountAfterBetting = engine.getPlayer().getChips();

		if (engine.getState() == BlackjackEngineState.PLAYER_TURN)
		{
			engine.playerSurrender();
			Assertions.assertTrue(nearlyEquals(chipAmountAfterBetting, engine.getPlayer().getChips(), BlackjackConstants.DEFAULT_CHIP_SCALE));
			Assertions.assertTrue(engine.getActiveHandContext().isSurrendered());
		}

		engine.showdown();
		advanceToEndOfRoundAfterShowdown();

		Assertions.assertTrue(nearlyEquals(
			chipAmountAfterBetting.add(betAmount.divide(BigDecimal.TWO, MathContext.DECIMAL128)).stripTrailingZeros(),
			engine.getPlayer().getChips(),
			BlackjackConstants.DEFAULT_CHIP_SCALE
		));
	}
}