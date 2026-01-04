package com.github.kqfall1.java.blackjackEngine.controllers.config;

import com.github.kqfall1.java.blackjackEngine.controllers.CustomDeckTest;
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
		dealer.hit();
		dealer.getHand().addCards(dealer.hit());
		dealer.hit();
		dealer.getHand().addCards(dealer.hit());

		Assertions.assertFalse(super.config.getDealerHitsOnSoft17());
		Assertions.assertFalse(super.config.isDealerTurnActive(
			EngineState.DEALER_TURN,
			dealer
		));
	}
}