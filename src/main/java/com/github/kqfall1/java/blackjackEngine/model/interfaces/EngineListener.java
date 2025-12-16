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
	void onBetPlaced(Player player, PlayerHand playerHand);
	void onCardDealtToDealer(Card card, Hand dealerHand);
	void onCardDealtToPlayer(Card card, PlayerHand playerHand);
	void onDrawingRoundCompletedDealer();
	void onDrawingRoundCompletedPlayer(PlayerHand playerHand);
	void onInsuranceBetOpportunityDetected();
	void onInsuranceBetResolved(boolean wasSuccessful);
	void onStateChanged(EngineState oldState, EngineState newState);
}