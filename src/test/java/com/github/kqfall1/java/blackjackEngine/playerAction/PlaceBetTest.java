package com.github.kqfall1.java.blackjackEngine.playerAction;

import com.github.kqfall1.java.blackjackEngine.engine.EngineTest;
import com.github.kqfall1.java.blackjackEngine.model.engine.BlackjackConstants;
import com.github.kqfall1.java.blackjackEngine.model.enums.BlackjackEngineState;
import java.math.BigDecimal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

public final class PlaceBetTest extends EngineTest
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
		final var initialChipAmount = engine.getPlayer().getChips();
		final var betAmount = advanceToPlayerTurn(initialChipAmount);

		if (engine.getState() == BlackjackEngineState.PLAYER_TURN)
		{
			engine.playerStand();
			Assertions.assertTrue(initialChipAmount.compareTo(engine.getPlayer().getChips()) > 0);
			Assertions.assertNotNull(engine.getActiveHandContext().getBet());
			Assertions.assertNotNull(engine.getActiveHandContext().getPot());
			Assertions.assertTrue(nearlyEquals(
				betAmount.stripTrailingZeros(),
				engine.getActiveHandContext().getBet().getAmount(),
				BlackjackConstants.DEFAULT_CHIP_SCALE
			));
			Assertions.assertTrue(nearlyEquals(
				betAmount.multiply(BigDecimal.TWO).stripTrailingZeros(),
				engine.getActiveHandContext().getPot().getAmount(),
				BlackjackConstants.DEFAULT_CHIP_SCALE
			));
		}

		advanceThroughShowdownsAfterPlayerTurn();
		advanceToEndOfRoundAfterShowdown();
	}
}