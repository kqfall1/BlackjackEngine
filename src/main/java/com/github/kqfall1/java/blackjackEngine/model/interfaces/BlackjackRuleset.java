package com.github.kqfall1.java.blackjackEngine.model.interfaces;

import com.github.kqfall1.java.blackjackEngine.model.cards.Rank;
import com.github.kqfall1.java.blackjackEngine.model.engine.BlackjackConstants;
import com.github.kqfall1.java.blackjackEngine.model.engine.EngineState;
import com.github.kqfall1.java.blackjackEngine.model.entities.Dealer;
import com.github.kqfall1.java.blackjackEngine.model.entities.Player;
import com.github.kqfall1.java.blackjackEngine.model.hands.Hand;
import com.github.kqfall1.java.blackjackEngine.model.hands.HandContext;

/**
 * Defines the methods that blackjack rule objects must have a valid implementation for to
 * be serviced by any {@code BlackjackEngine}.
 *
 * @author kqfall1
 * @since 03/01/2026
 */
public interface BlackjackRuleset
{
	default Rank[] getExcludedRanks(Rank... ranks)
	{
		return new Rank[0];
	}

	default HandContext[] getHandContextShowdownOrder(Player player)
	{
		return player.getContexts().toArray(new HandContext[0]);
	}

	default boolean isDealerSecondCardFaceDown()
	{
		return true;
	}

	default boolean isHandBlackjack(Hand hand)
	{
		return hand.getCards().size() == BlackjackConstants.INITIAL_CARD_COUNT;
	}

	default boolean isHandBusted(Hand hand)
	{
		return hand.getScore() > BlackjackConstants.DEFAULT_TOP_SCORE;
	}

	boolean isDealerTurnActive(EngineState currentState, Dealer dealer);
	boolean isDoubleDownPossible(HandContext activeHandContext, EngineState currentState,
								 Player player);
	boolean isInsuranceBetPossible(HandContext activeHandContext, EngineState currentState,
									  Player player, Hand dealerHand);
	boolean isSplitPossible(HandContext activeHandContext, EngineState currentState,
							   int activeHandContextIndex, Player player);
	boolean isSurrenderingPossible(HandContext activeHandContext,
									  EngineState currentState);

	default boolean shouldDealerPeekForBlackjack()
	{
		return true;
	}
}