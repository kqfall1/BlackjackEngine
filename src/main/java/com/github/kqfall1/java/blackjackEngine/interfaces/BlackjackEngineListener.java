package com.github.kqfall1.java.blackjackEngine.interfaces;

import com.github.kqfall1.java.blackjackEngine.cards.Card;
import com.github.kqfall1.java.blackjackEngine.enums.BlackjackEngineState;
import com.github.kqfall1.java.blackjackEngine.hands.Hand;
import com.github.kqfall1.java.blackjackEngine.hands.HandContext;
import java.math.BigDecimal;

/**
 * Defines the methods that application-controlling classes require to respond
 * to internal {@code BlackjackEngine} events and exceptions.
 *
 * @author kqfall1
 * @since 14/12/2025
 */
public interface BlackjackEngineListener
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
	void onPlayerSplit(HandContext currentHand, HandContext splitHand);
	void onReset();
	void onShowdownCompleted(Hand dealerHand, HandContext handContext, boolean playerWon, BigDecimal playerWinnings);
	void onShowdownStarted(Hand dealerHand, HandContext handContext);
	void onStateChanged(BlackjackEngineState oldState);
}