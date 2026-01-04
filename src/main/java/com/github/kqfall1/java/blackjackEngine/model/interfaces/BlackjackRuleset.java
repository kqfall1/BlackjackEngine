package com.github.kqfall1.java.blackjackEngine.model.interfaces;

import com.github.kqfall1.java.blackjackEngine.model.betting.PayoutRatio;
import com.github.kqfall1.java.blackjackEngine.model.enums.Rank;
import com.github.kqfall1.java.blackjackEngine.model.engine.BlackjackConstants;
import com.github.kqfall1.java.blackjackEngine.model.enums.EngineState;
import com.github.kqfall1.java.blackjackEngine.model.entities.Dealer;
import com.github.kqfall1.java.blackjackEngine.model.entities.Player;
import com.github.kqfall1.java.blackjackEngine.model.hands.Hand;
import com.github.kqfall1.java.blackjackEngine.model.hands.HandContext;
import java.util.HashMap;
import java.util.Map;

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

	default Map<String, PayoutRatio> getPayoutRatios()
	{
		Map<String, PayoutRatio> payoutRatios = new HashMap<>();

		payoutRatios.put(BlackjackConstants.BLACKJACK_RATIO_KEY, BlackjackConstants.BLACKJACK_RATIO);
		payoutRatios.put(BlackjackConstants.INSURANCE_RATIO_LABEL, BlackjackConstants.INSURANCE_RATIO);
		payoutRatios.put(BlackjackConstants.PUSH_RATIO_LABEL, BlackjackConstants.PUSH_RATIO);
		payoutRatios.put(BlackjackConstants.SURRENDER_RATIO_LABEL, BlackjackConstants.SURRENDER_RATIO);

		return payoutRatios;
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