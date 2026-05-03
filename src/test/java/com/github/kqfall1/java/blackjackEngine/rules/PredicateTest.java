package com.github.kqfall1.java.blackjackEngine.rules;

import com.github.kqfall1.java.blackjackEngine.engine.CustomDeckTest;
import com.github.kqfall1.java.blackjackEngine.model.enums.BlackjackEngineState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class PredicateTest extends CustomDeckTest
{
	@BeforeEach
	@Override
	public void init()
	{
		_initCardsForNormalShowdown();
		initDependencies();
		initEngine();
		engine.getDealer().setCardSource(testDeck);
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	public void main()
	{
		while (engine.getState() != BlackjackEngineState.END)
		{
			placeRandomHandBet(engine.getPlayer().getChips());
			engine.deal();
			verifyStandardAssumptions();
			engine.advanceAfterDeal();
			verifyStandardAssumptions();
			declinePossibleInsuranceBet();

			if (engine.getState() == BlackjackEngineState.PLAYER_TURN)
			{
				engine.playerHit();
				verifyStandardAssumptions();

				if (engine.getState() == BlackjackEngineState.PLAYER_TURN)
				{
					engine.playerStand();
				}
			}

			if (engine.getState() == BlackjackEngineState.DEALER_TURN)
			{
				verifyPlayerActionsAreIllegal();
				engine.advanceAfterDealerTurn();
			}

			engine.showdown();
			verifyStandardAssumptions();
			advanceToEndOfRoundAfterShowdown();
		}
	}

	private void verifyStandardAssumptions()
	{
		verifyPlayerActionsAreIllegal();
		Assertions.assertFalse(engine.getRuleset().isDealerTurnActive(engine.getState(), engine.getDealer()));
	}

	private void verifyPlayerActionsAreIllegal()
	{
		Assertions.assertFalse(
			engine.getRuleset().isInsuranceBetPossible(
				engine.getActiveHandContext(),
				engine.getState(),
				engine.getPlayer(),
				engine.getDealer().getHand()
			)
			&& engine.getRuleset().isDoublingDownPossible(
				engine.getActiveHandContext(),
				engine.getState(),
				engine.getPlayer()
			)
			&& engine.getRuleset().isSplittingPossible(
				engine.getActiveHandContext(),
				engine.getState(),
				engine.getActiveHandContextIndex(),
				engine.getPlayer()
			)
			&& engine.getRuleset().isSurrenderingPossible(
				engine.getActiveHandContext(),
				engine.getState()
			)
		);
	}
}