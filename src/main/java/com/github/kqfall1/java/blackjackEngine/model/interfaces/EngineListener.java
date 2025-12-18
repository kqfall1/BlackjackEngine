package com.github.kqfall1.java.blackjackEngine.model.interfaces;

import com.github.kqfall1.java.blackjackEngine.model.cards.Card;
import com.github.kqfall1.java.blackjackEngine.model.engine.EngineState;
import java.math.BigDecimal;

/**
 * Defines the methods that application-controlling classes require to respond
 * to internal {@code BlackjackEngine} events and exceptions.
 *
 * @author kqfall1
 * @since 14/12/2025
 */
public interface EngineListener
{
	void onBetPlaced();
	void onBettingRoundCompleted();
	void onBettingRoundStarted();
	void onCardDealtToDealer(Card card);
	void onCardDealtToPlayer(Card card);
	void onDrawingRoundCompletedDealer();
	void onDrawingRoundCompletedPlayer();
	void onDrawingRoundStartedDealer();
	void onDrawingRoundStartedPlayer();
	void onGameCompleted();
	void onGameStarted();
	void onInsuranceBetOpportunityDetected();
	void onInsuranceBetResolved(BigDecimal playerWinnings);
	void onReset();
	void onShowdownCompleted(boolean playerWon, BigDecimal playerWinnings);
	void onShowdownStarted();
	void onStateChanged(EngineState oldState);
}