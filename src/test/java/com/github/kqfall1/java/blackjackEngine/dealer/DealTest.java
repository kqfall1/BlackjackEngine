package com.github.kqfall1.java.blackjackEngine.dealer;

import com.github.kqfall1.java.blackjackEngine.engine.EngineTest;
import com.github.kqfall1.java.blackjackEngine.model.engine.BlackjackConstants;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class DealTest extends EngineTest
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
		placeRandomHandBet(engine.getPlayer().getChips());
		engine.deal();
		assertTrue(engine.getDealer().getHand() != null && engine.getActiveHandContext().getHand() != null);
		assertTrue(
			engine.getDealer().getHand().getCards().size() == BlackjackConstants.INITIAL_CARD_COUNT
			&& engine.getActiveHandContext().getHand().getCards().size() == BlackjackConstants.INITIAL_CARD_COUNT
		);
		assertTrue(
			engine.getDealer().getHand().getScore() <= BlackjackConstants.TOP_SCORE
			&& engine.getActiveHandContext().getHand().getScore() <= BlackjackConstants.TOP_SCORE
		);
	}
}