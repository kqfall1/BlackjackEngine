package com.github.kqfall1.java.blackjackEngine.rules.soft17;

import com.github.kqfall1.java.blackjackEngine.engine.CustomDeckTest;
import com.github.kqfall1.java.blackjackEngine.model.entities.Dealer;
import com.github.kqfall1.java.blackjackEngine.model.enums.EngineState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class DealerStandsOnSoft17Test extends CustomDeckTest
{
	@BeforeEach
	public void init()
	{
		super.initCardsForDealerSoft17();
		super.initDependencies();
	}

	@RepeatedTest(TEST_ITERATIONS)
	public void main()
	{
		final var dealer = new Dealer(testDeck);
		dealer.getCardSource().draw();
		dealer.getHand().addCards(dealer.getCardSource().draw());
		dealer.getCardSource().draw();
		dealer.getHand().addCards(dealer.getCardSource().draw());

		Assertions.assertFalse(super.ruleset.getConfig().getShouldDealerHitOnSoft17());
		Assertions.assertFalse(super.ruleset.isDealerTurnActive(
			EngineState.DEALER_TURN,
			dealer
		));
	}
}