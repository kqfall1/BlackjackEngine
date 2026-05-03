package com.github.kqfall1.java.blackjackEngine.playerAction;

import com.github.kqfall1.java.blackjackEngine.engine.EngineTest;
import com.github.kqfall1.java.blackjackEngine.model.engine.BlackjackConstants;
import com.github.kqfall1.java.blackjackEngine.model.enums.BlackjackEngineState;
import java.math.BigDecimal;
import java.math.MathContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class DoubleDownTest extends EngineTest
{
	@BeforeEach
	@Override
	public void init()
	{
		initDependencies();
		initEngine();
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	public void main()
	{
		final var initialBetAmount = advanceToPlayerTurn(INITIAL_PLAYER_CHIP_AMOUNT
			.subtract(engine.getRuleset().getConfig().getMinimumBetAmount())
			.divide(BigDecimal.TWO, MathContext.DECIMAL128));

		if (engine.getState() == BlackjackEngineState.PLAYER_TURN)
		{
			Assertions.assertFalse(engine.getActiveHandContext().isAltered());
			engine.playerDoubleDown();
			Assertions.assertTrue(
				nearlyEquals(
					initialBetAmount
						.multiply(BigDecimal.valueOf(4))
						.stripTrailingZeros(),
					engine.getActiveHandContext().getPot().getAmount(),
					BlackjackConstants.DEFAULT_CHIP_SCALE
				)
			);
			Assertions.assertTrue(
				engine.getActiveHandContext().getHand().getCards().size()
					== BlackjackConstants.INITIAL_CARD_COUNT + 1
				&& engine.getActiveHandContext().isAltered()
			);
		}

		advanceThroughShowdownsAfterPlayerTurn();
		advanceToEndOfRoundAfterShowdown();
	}
}