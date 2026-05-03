package com.github.kqfall1.java.blackjackEngine.rules.soft17;

import com.github.kqfall1.java.blackjackEngine.engine.CustomDeckTest;
import com.github.kqfall1.java.blackjackEngine.model.entities.Dealer;
import com.github.kqfall1.java.blackjackEngine.model.enums.BlackjackEngineState;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.NoMoreCardsException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class DealerStandsOnSoft17Test extends CustomDeckTest
{
	@BeforeEach
	public void init()
	{
		initCardsForDealerSoft17();
		initDependencies();
	}

	@RepeatedTest(TEST_ITERATIONS)
	public void main()
	{
		final var dealer = new Dealer(testDeck);
		try
		{
			dealer.getCardSource().draw();
			dealer.getHand().addCards(dealer.getCardSource().draw());
			dealer.getCardSource().draw();
			dealer.getHand().addCards(dealer.getCardSource().draw());
		}
		catch (NoMoreCardsException ignored) {}
		Assertions.assertFalse(ruleset.getConfig().getShouldDealerHitOnSoft17());
		Assertions.assertFalse(ruleset.isDealerTurnActive(BlackjackEngineState.DEALER_TURN, dealer));
	}
}