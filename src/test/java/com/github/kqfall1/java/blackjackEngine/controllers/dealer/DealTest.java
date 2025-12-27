package com.github.kqfall1.java.blackjackEngine.controllers.dealer;

import com.github.kqfall1.java.blackjackEngine.controllers.EngineTestTemplate;
import com.github.kqfall1.java.blackjackEngine.model.engine.StandardRuleConfig;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import java.io.IOException;

final class DealTest extends EngineTestTemplate
{
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/DealTest.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.dealer.DealTest.log";

	@BeforeEach
	@Override
	public void init() throws InsufficientChipsException, IOException
	{
		super.initDependencies();
		super.initEngine(LOG_FILE_PATH, LOGGER_NAME, null);
	}

	@Override
	@RepeatedTest(TEST_ITERATIONS)
	public void main() throws Exception
	{
		super.placeRandomHandBet(super.engine.getPlayer().getChips());
		super.engine.deal();

		assertTrue(
			super.engine.getDealer().getHand() != null
			&& super.engine.getActiveHandContext().getHand() != null
		);
		assertTrue(
			super.engine.getDealer().getHand().getCards().size() == StandardRuleConfig.INITIAL_CARD_COUNT
			&& super.engine.getActiveHandContext().getHand().getCards().size() == StandardRuleConfig.INITIAL_CARD_COUNT
		);
		assertTrue(
			super.engine.getDealer().getHand().getScore() <= StandardRuleConfig.TOP_SCORE
			&& super.engine.getActiveHandContext().getHand().getScore() <= StandardRuleConfig.TOP_SCORE
		);
	}
}