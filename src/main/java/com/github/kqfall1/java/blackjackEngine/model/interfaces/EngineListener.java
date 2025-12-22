package com.github.kqfall1.java.blackjackEngine.model.interfaces;

import com.github.kqfall1.java.blackjackEngine.model.cards.Card;
import com.github.kqfall1.java.blackjackEngine.model.engine.EngineState;
import com.github.kqfall1.java.blackjackEngine.model.hands.Hand;
import com.github.kqfall1.java.blackjackEngine.model.hands.HandContext;
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
	void onBetPlaced(HandContext handContext);
	void onBettingRoundCompleted();
	void onBettingRoundStarted();
	void onCardDealtToDealer(Card card, Hand dealerHand, boolean isFaceUp);
	void onCardDealtToPlayer(Card card, HandContext handContext);
	void onDrawingRoundCompletedDealer(Hand dealerHand);
	void onDrawingRoundCompletedPlayer(HandContext handContext);
	void onDrawingRoundStartedDealer(Hand dealerHand);
	void onDrawingRoundStartedPlayer(HandContext handContext);
	void onGameCompleted();
	void onGameStarted();
	void onInsuranceBetOpportunityDetected(Card dealerUpCard);
	void onInsuranceBetResolved(boolean wasSuccessful, BigDecimal playerWinnings);
	void onPlayerSplit(HandContext previousHand, HandContext splitHand);
	void onReset();
	void onShowdownCompleted(Hand dealerHand, HandContext handContext, boolean playerWon,
							 BigDecimal playerWinnings);
	void onShowdownStarted(Hand dealerHand, HandContext handContext);
	void onStateChanged(EngineState oldState);
}