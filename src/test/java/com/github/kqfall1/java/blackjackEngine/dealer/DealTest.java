package com.github.kqfall1.java.blackjackEngine.dealer;

import com.github.kqfall1.java.blackjackEngine.engine.EngineTest;
import com.github.kqfall1.java.blackjackEngine.model.enums.EngineState;
import com.github.kqfall1.java.blackjackEngine.model.engine.BlackjackConstants;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class DealTest extends EngineTest
{
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/DealTest.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.dealer.DealTest.log";

	@BeforeEach
	@Override
	public void init() {
		super.initDependencies();
		super.initEngine(LOG_FILE_PATH, LOGGER_NAME);
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	public void main()
	{
		super.placeRandomHandBet(super.engine.getPlayer().getChips());
		super.engine.deal();

		assertTrue(
			super.engine.getDealer().getHand() != null
			&& super.engine.getActiveHandContext().getHand() != null
		);
		assertTrue(
			super.engine.getDealer().getHand().getCards().size() == BlackjackConstants.INITIAL_CARD_COUNT
			&& super.engine.getActiveHandContext().getHand().getCards().size() == BlackjackConstants.INITIAL_CARD_COUNT
		);
		assertTrue(
			super.engine.getDealer().getHand().getScore() <= BlackjackConstants.DEFAULT_TOP_SCORE
			&& super.engine.getActiveHandContext().getHand().getScore() <= BlackjackConstants.DEFAULT_TOP_SCORE
		);

		super.engine.advanceAfterDeal();
		super.declinePossibleInsuranceBet();

		if (super.engine.getState() == EngineState.PLAYER_TURN)
		{
			super.engine.playerStand();
		}

		super.advanceToEndOfRound();
	}
}