package com.github.kqfall1.java.blackjackEngine.model.interfaces;

import com.github.kqfall1.java.blackjackEngine.model.betting.PayoutRatio;
import com.github.kqfall1.java.blackjackEngine.model.engine.BlackjackRulesetConfiguration;
import com.github.kqfall1.java.blackjackEngine.model.engine.BlackjackConstants;
import com.github.kqfall1.java.blackjackEngine.model.enums.BlackjackEngineState;
import com.github.kqfall1.java.blackjackEngine.model.enums.Rank;
import com.github.kqfall1.java.blackjackEngine.model.entities.Dealer;
import com.github.kqfall1.java.blackjackEngine.model.entities.Player;
import com.github.kqfall1.java.blackjackEngine.model.hands.Hand;
import com.github.kqfall1.java.blackjackEngine.model.hands.HandContext;
import java.util.Map;

/**
 * Defines the methods that blackjack rule objects require to be serviced by a {@code BlackjackEngine}.
 *
 * @author kqfall1
 * @since 03/01/2026
 */
public interface BlackjackRuleset
{
	BlackjackRulesetConfiguration getConfig();

	default Rank[] getIncludedRanks()
	{
		return Rank.values();
	}

	default HandContext[] getHandContextsInShowdownOrder(Player player)
	{
		return player.getContexts().toArray(new HandContext[0]);
	}

	default Map<String, PayoutRatio> getPayoutRatios()
	{
		return Map.of(
			BlackjackConstants.BLACKJACK_RATIO_KEY, BlackjackConstants.BLACKJACK_RATIO,
			BlackjackConstants.INSURANCE_RATIO_KEY, BlackjackConstants.INSURANCE_RATIO,
			BlackjackConstants.PUSH_RATIO_KEY, BlackjackConstants.PUSH_RATIO,
			BlackjackConstants.SURRENDER_RATIO_KEY, BlackjackConstants.SURRENDER_RATIO
		);
	}

	default boolean isDealerSecondCardFaceDown()
	{
		return true;
	}

	default boolean isHandBlackjack(Hand hand)
	{
		return hand.getScore() == BlackjackConstants.TOP_SCORE && hand.getCards().size() == BlackjackConstants.INITIAL_CARD_COUNT;
	}

	default boolean isHandBusted(Hand hand)
	{
		return hand.getScore() > BlackjackConstants.TOP_SCORE;
	}

	boolean isDealerTurnActive(BlackjackEngineState currentState, Dealer dealer);
	boolean isDoublingDownPossible(HandContext activeHandContext, BlackjackEngineState currentState,
								   Player player);
	default boolean isHittingPossible(HandContext activeHandContext, BlackjackEngineState currentState)
	{
		return activeHandContext.getHand().getScore() <= BlackjackConstants.TOP_SCORE && currentState == BlackjackEngineState.PLAYER_TURN;
	}
	boolean isInsuranceBetPossible(HandContext activeHandContext, BlackjackEngineState currentState,
									  Player player, Hand dealerHand);
	boolean isSplittingPossible(HandContext activeHandContext, BlackjackEngineState currentState,
								int activeHandContextIndex, Player player);
	boolean isSurrenderingPossible(HandContext activeHandContext, BlackjackEngineState currentState, Player player);

	default boolean shouldDealerPeekForBlackjack()
	{
		return true;
	}
}