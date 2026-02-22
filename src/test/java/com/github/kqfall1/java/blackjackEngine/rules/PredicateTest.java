package com.github.kqfall1.java.blackjackEngine.rules;

import com.github.kqfall1.java.blackjackEngine.engine.CustomDeckTest;
import com.github.kqfall1.java.blackjackEngine.model.enums.EngineState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class PredicateTest extends CustomDeckTest
{
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/RulePredicateTest.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.config.RulePredicateTest.log";

	@BeforeEach
	@Override
	public void init() {
		super._initCardsForNormalShowdown();
		super.initDependencies();
		super.initEngine(LOG_FILE_PATH, LOGGER_NAME);
		super.engine.getDealer().setCardSource(testDeck);
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	public void main()
	{
		while (super.engine.getState() != EngineState.END)
		{
			super.placeRandomHandBet(super.engine.getPlayer().getChips());
			super.engine.deal();
			verifyStandardAssumptions();
			super.engine.advanceAfterDeal();

			Assertions.assertFalse(
				super.ruleset.isInsuranceBetPossible(
					super.engine.getActiveHandContext(),
					super.engine.getState(),
					super.engine.getPlayer(),
					super.engine.getDealer().getHand()
				)
				&& super.ruleset.isSplitPossible(
					super.engine.getActiveHandContext(),
					super.engine.getState(),
					super.engine.getActiveHandContextIndex(),
					super.engine.getPlayer()
				)
				&& super.ruleset.isSurrenderingPossible(
					super.engine.getActiveHandContext(),
					super.engine.getState()
				)
			);

			Assertions.assertFalse(
				super.ruleset.isDealerTurnActive(
					super.engine.getState(),
					super.engine.getDealer()
				)
			);

			super.declinePossibleInsuranceBet();

			if (super.engine.getState() == EngineState.PLAYER_TURN)
			{
				super.engine.playerHit();
				verifyStandardAssumptions();

				if (super.engine.getState() == EngineState.PLAYER_TURN)
				{
					super.engine.playerStand();
				}
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
			super.ruleset.isDealerTurnActive(
				super.engine.getState(),
				super.engine.getDealer()
			)
		);
	}

	private void verifyPlayerActionsAreIllegal()
	{
		Assertions.assertFalse(
			super.ruleset.isInsuranceBetPossible(
				super.engine.getActiveHandContext(),
				super.engine.getState(),
				super.engine.getPlayer(),
				super.engine.getDealer().getHand()
			)
			&& super.ruleset.isDoubleDownPossible(
				super.engine.getActiveHandContext(),
				super.engine.getState(),
				super.engine.getPlayer()
			)
			&& super.ruleset.isSplitPossible(
				super.engine.getActiveHandContext(),
				super.engine.getState(),
				super.engine.getActiveHandContextIndex(),
				super.engine.getPlayer()
			)
			&& super.ruleset.isSurrenderingPossible(
				super.engine.getActiveHandContext(),
				super.engine.getState()
			)
		);
	}
}