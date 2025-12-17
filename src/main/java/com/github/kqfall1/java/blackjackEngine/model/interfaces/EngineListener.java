package com.github.kqfall1.java.blackjackEngine.model.interfaces;

import com.github.kqfall1.java.blackjackEngine.model.cards.Card;
import com.github.kqfall1.java.blackjackEngine.model.engine.EngineState;
import com.github.kqfall1.java.blackjackEngine.model.entities.Player;
import com.github.kqfall1.java.blackjackEngine.model.hands.Hand;
import com.github.kqfall1.java.blackjackEngine.model.hands.PlayerHand;

/**
 * Defines the methods that application-controlling classes require to respond
 * to internal {@code GameEngine} events and exceptions.
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
	void onInsuranceBetResolved(boolean playerWon);
	void onReset();
	void onShowdownCompleted(boolean playerWon);
	void onShowdownStarted();
	void onStateChanged(EngineState oldState);
}