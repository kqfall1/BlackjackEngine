package com.github.kqfall1.java.blackjackEngine.model.engine;

import com.github.kqfall1.java.blackjackEngine.model.entities.Dealer;
import com.github.kqfall1.java.blackjackEngine.model.entities.Player;
import com.github.kqfall1.java.blackjackEngine.model.enums.EngineState;
import com.github.kqfall1.java.blackjackEngine.model.enums.HandContextType;
import com.github.kqfall1.java.blackjackEngine.model.enums.Rank;
import com.github.kqfall1.java.blackjackEngine.model.hands.Hand;
import com.github.kqfall1.java.blackjackEngine.model.hands.HandContext;
import com.github.kqfall1.java.blackjackEngine.model.interfaces.BlackjackRuleset;

/**
 * Represents the standard, North American rules of blackjack; determines valid game actions,
 * especially given certain {@code BlackjackEngine} states.
 *
 * @author kqfall1
 * @since 03/01/2025
 */
public final class StandardBlackjackRuleset implements BlackjackRuleset
{
	private final BlackjackRulesetConfiguration config;

	public StandardBlackjackRuleset(BlackjackRulesetConfiguration config)
	{
		assert config != null : "config == null";
		this.config = config;
	}

	@Override
	public BlackjackRulesetConfiguration getConfig()
	{
		return config;
	}

	@Override
	public boolean isDealerTurnActive(EngineState currentState, Dealer dealer)
	{
		final var MINIMUM_SCORE_TO_STAND =
			config.getShouldDealerHitOnSoft17()
			&& dealer.getHand().getScore() == BlackjackConstants.DEFAULT_DEALER_MINIMUM_STAND_SCORE
			&& dealer.getHand().isSoft()
				? BlackjackConstants.DEFAULT_DEALER_MINIMUM_STAND_SCORE + 1
				: BlackjackConstants.DEFAULT_DEALER_MINIMUM_STAND_SCORE;

		return currentState == EngineState.DEALER_TURN
			&& dealer.getHand().getScore() < MINIMUM_SCORE_TO_STAND;
	}

	@Override
	public boolean isDoubleDownPossible(HandContext activeHandContext, EngineState currentState,
										Player player)
	{
		return currentState == EngineState.PLAYER_TURN
			&& !activeHandContext.isAltered()
			&& (activeHandContext.getType() == HandContextType.MAIN
				|| config.isDoublingDownOnSplitHandsAllowed())
			&& player.getChips().compareTo(activeHandContext.getBet().getAmount()) >= 0;
	}

	@Override
	public boolean isInsuranceBetPossible(HandContext activeHandContext, EngineState currentState,
										  Player player, Hand dealerHand)
	{
		return (currentState == EngineState.DEALING || currentState == EngineState.INSURANCE_CHECK)
			&& !activeHandContext.isAltered()
			&& player.getChips().compareTo(activeHandContext.getBet().getHalf()) >= 0
			&& dealerHand.getCards().getFirst().getRank() == Rank.ACE
			&& player.getContexts().size() == 1;
	}

	@Override
	public boolean isSplitPossible(HandContext activeHandContext, EngineState currentState,
								   int activeHandContextIndex, Player player)
	{
		return currentState == EngineState.PLAYER_TURN
			&& !activeHandContext.isAltered()
			&& activeHandContext.getHand().isPocketPair()
			&& (activeHandContext.getHand().getCards().getFirst().getRank() != Rank.ACE
				|| getConfig().isSplittingAcesAllowed())
		    && activeHandContextIndex < config.getMaximumSplitCount()
			&& player.getChips().compareTo(activeHandContext.getBet().getAmount()) >= 0;
	}

	@Override
	public boolean isSurrenderingPossible(HandContext activeHandContext, EngineState currentState)
	{
		return currentState == EngineState.PLAYER_TURN
			&& config.isSurrenderingAllowed()
			&& !activeHandContext.isAltered()
			&& (activeHandContext.getType() == HandContextType.MAIN
				|| config.isSurrenderingOnSplitHandsAllowed());
	}

	@Override
	public String toString()
	{
		return String.format(
			"%s[config=%s]",
			getClass().getName(),
			config
		);
	}
}