package com.github.kqfall1.java.blackjackEngine.controllers.insurance;

import com.github.kqfall1.java.blackjackEngine.controllers.CustomDeckTest;
import com.github.kqfall1.java.blackjackEngine.model.engine.EngineState;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class InsuranceBetDeclineAndSplitTest extends CustomDeckTest
{
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/InsuranceBetDeclineAndSplitTest.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.insurance.InsuranceBetDeclineAndSplitTest.log";

	@BeforeEach
	@Override
	public void init() throws InsufficientChipsException, IOException
	{
		super.initCardsForInsuranceAndSplitting7s();
		super.initDependencies();
		super.config.setMaximumSplitCount(MAXIMUM_SPLIT_COUNT);
		super.initEngine(LOG_FILE_PATH, LOGGER_NAME);
		super.engine.getDealer().setDeck(testDeck);
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	public void main() throws Exception
	{
		super.placeRandomHandBet(
			super.engine.getPlayer().getChips().divide(
				BigDecimal.valueOf(MAXIMUM_SPLIT_COUNT + 2),
				MathContext.DECIMAL128
			)
		);
		super.engine.deal();
		super.engine.advanceAfterDeal();
		super.declinePossibleInsuranceBet();

		if (super.engine.getState() == EngineState.PLAYER_TURN)
		{
			super.initSplitHands();

			for (int count = 0
				; count < super.engine.getConfig().getMaximumSplitCount() + 1
				; count ++)
			{
				super.engine.playerStand();
			}

			super.engine.advanceAfterPlayerTurn();
			super.advanceToEndAfterPotentialDealerTurn();
		}
	}
}