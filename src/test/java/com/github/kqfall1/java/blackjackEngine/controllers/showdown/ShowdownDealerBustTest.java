package com.github.kqfall1.java.blackjackEngine.controllers.showdown;

import com.github.kqfall1.java.blackjackEngine.controllers.CustomDeckTest;
import com.github.kqfall1.java.blackjackEngine.model.engine.StandardRuleConfig;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import java.io.IOException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

final class ShowdownDealerBustTest extends CustomDeckTest
{
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/ShowdownDealerBustTest.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.playerAction.ShowdownDealerBustTest.log";

	@BeforeEach
	@Override
	public void init() throws InsufficientChipsException, IOException
	{
		super._initCardsForBust();
		super.initDependencies();
		super.initEngine(LOG_FILE_PATH, LOGGER_NAME);
		super.engine.getDealer().setCardSource(testDeck);
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	public void main() throws Exception
	{
		BigDecimal chipAmountAfterBetting;
		BigDecimal potAmount;

		super.advanceToDealerTurn(super.engine.getPlayer().getChips());
		chipAmountAfterBetting = super.engine.getPlayer().getChips();
		potAmount = super.engine.getActiveHandContext().getPot().getAmount();
		Assertions.assertTrue(super.engine.getDealer().getHand().isBusted());
		super.engine.advanceAfterDealerTurn();
		super.engine.advanceAfterShowdown();

		Assertions.assertTrue(
			nearlyEquals(
				chipAmountAfterBetting
					.add(potAmount)
					.stripTrailingZeros(),
				super.engine.getPlayer().getChips(),
				StandardRuleConfig.CHIP_SCALE
			)
		);

		super.engine.advanceAfterReset();
	}
}