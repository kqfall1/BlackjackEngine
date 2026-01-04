package com.github.kqfall1.java.blackjackEngine.controllers.config;

import com.github.kqfall1.java.blackjackEngine.controllers.CustomDeckTest;
import com.github.kqfall1.java.blackjackEngine.model.enums.EngineState;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class PredicateTest extends CustomDeckTest
{
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/RulePredicateTest.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.config.RulePredicateTest.log";

	@BeforeEach
	@Override
	public void init() throws InsufficientChipsException, IOException
	{
		super._initCardsForNormalShowdown();
		super.initDependencies();
		super.initEngine(LOG_FILE_PATH, LOGGER_NAME);
		super.engine.getDealer().setCardSource(testDeck);
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	public void main() throws Exception
	{
		while (super.engine.getState() != EngineState.END)
		{
			super.placeRandomHandBet(super.engine.getPlayer().getChips());
			super.engine.deal();
			verifyStandardAssumptions();
			super.engine.advanceAfterDeal();

			Assertions.assertFalse(
				super.engine.getConfig().isInsuranceBetPossible(
					super.engine.getActiveHandContext(),
					super.engine.getState(),
					super.engine.getPlayer(),
					super.engine.getDealer().getHand()
				)
				&& super.engine.getConfig().isSplitPossible(
					super.engine.getActiveHandContext(),
					super.engine.getState(),
					super.engine.getActiveHandContextIndex(),
					super.engine.getPlayer()
				)
				&& super.engine.getConfig().isSurrenderingPossible(
					super.engine.getActiveHandContext(),
					super.engine.getState()
				)
			);

			Assertions.assertFalse(
				super.engine.getConfig().isDealerTurnActive(
					super.engine.getState(),
					super.engine.getDealer()
				)
			);

			super.declinePossibleInsuranceBet();

			if (super.engine.getState() == EngineState.PLAYER_TURN)
			{
				super.engine.playerHit();
				verifyStandardAssumptions();
				super.engine.playerStand();
				super.engine.advanceAfterPlayerTurn();
			}

			if (super.engine.getState() == EngineState.DEALER_TURN)
			{
				verifyPlayerActionsAreIllegal();
				super.engine.advanceAfterDealerTurn();
			}

			verifyStandardAssumptions();
			super.engine.advanceAfterShowdown();
			super.engine.advanceAfterReset();
		}
	}

	private void verifyStandardAssumptions()
	{
		verifyPlayerActionsAreIllegal();

		Assertions.assertFalse(
			super.engine.getConfig().isDealerTurnActive(
				super.engine.getState(),
				super.engine.getDealer()
			)
		);
	}

	private void verifyPlayerActionsAreIllegal()
	{
		Assertions.assertFalse(
			super.engine.getConfig().isInsuranceBetPossible(
				super.engine.getActiveHandContext(),
				super.engine.getState(),
				super.engine.getPlayer(),
				super.engine.getDealer().getHand()
			)
			&& super.engine.getConfig().isDoubleDownPossible(
				super.engine.getActiveHandContext(),
				super.engine.getState(),
				super.engine.getPlayer()
			)
			&& super.engine.getConfig().isSplitPossible(
				super.engine.getActiveHandContext(),
				super.engine.getState(),
				super.engine.getActiveHandContextIndex(),
				super.engine.getPlayer()
			)
			&& super.engine.getConfig().isSurrenderingPossible(
				super.engine.getActiveHandContext(),
				super.engine.getState()
			)
		);
	}
}