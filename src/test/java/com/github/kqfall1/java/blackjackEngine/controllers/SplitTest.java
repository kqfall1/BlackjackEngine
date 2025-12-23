package com.github.kqfall1.java.blackjackEngine.controllers;

import com.github.kqfall1.java.blackjackEngine.model.cards.TestDeck;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import java.io.IOException;

abstract class SplitTest extends EngineTestTemplate
{
	private static final String LOG_FILE_PATH = "src/main/resources/tests/logs/SplitTest.log";
	private static final String LOGGER_NAME = "com.github.kqfall1.java.blackjackEngine.controllers.SplitTest.log";

	@BeforeEach
	void init() throws InsufficientChipsException, IOException
	{
		super.init();
		final var testDeck = new TestDeck();
		engine = new BlackjackEngine(config, LISTENER, LOG_FILE_PATH, LOGGER_NAME);
		engine.getDealer().setDeck(testDeck);
		engine.start();
	}

	@RepeatedTest(TEST_ITERATIONS)
	abstract void main() throws Exception;
}