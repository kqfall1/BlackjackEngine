package com.github.kqfall1.java.blackjackEngine.model.engine;

import com.github.kqfall1.java.blackjackEngine.model.cards.Rank;
import com.github.kqfall1.java.blackjackEngine.model.cards.Shoe;
import com.github.kqfall1.java.blackjackEngine.model.entities.Dealer;
import com.github.kqfall1.java.blackjackEngine.model.entities.Player;
import com.github.kqfall1.java.blackjackEngine.model.hands.Hand;
import com.github.kqfall1.java.blackjackEngine.model.hands.HandContext;
import com.github.kqfall1.java.blackjackEngine.model.hands.HandContextType;
import com.github.kqfall1.java.managers.InputManager;
import java.math.BigDecimal;

/**
 * A configuration object to be used by a {@code BlackjackEngine} to drive gameplay according
 * to the standard rules of blackjack.
 *
 * @author kqfall1
 * @since 14/12/2025
 */
public final class StandardRuleConfig
{
	private boolean dealerHitsOnSoft17;
	private boolean doublingDownOnSplitHandsAllowed;
	private boolean loggingEnabled;
	private int maximumSplitCount;
	private BigDecimal minimumBetAmount;
	private BigDecimal playerInitialChips;
	private double shoeCutoffPercentageNumerator;
	private int shoeDeckCount;
	private boolean surrenderingAllowed;
	private boolean surrenderingOnSplitHandsAllowed;

	public StandardRuleConfig()
	{
		setMaximumSplitCount(BlackjackConstants.DEFAULT_MAXIMUM_SPLIT_COUNT);
		setMinimumBetAmount(BlackjackConstants.DEFAULT_MINIMUM_BET_AMOUNT);
		setShoeCutoffPercentageNumerator(BlackjackConstants.DEFAULT_SHOE_CUTOFF_PERCENTAGE_NUMERATOR);
		setShoeDeckCount(BlackjackConstants.DEFAULT_SHOE_DECK_COUNT);
	}

	public boolean getDealerHitsOnSoft17()
	{
		return dealerHitsOnSoft17;
	}

	public int getMaximumSplitCount()
	{
		return maximumSplitCount;
	}

	public BigDecimal getMinimumBetAmount()
	{
		return minimumBetAmount;
	}

	public BigDecimal getPlayerInitialChips()
	{
		return playerInitialChips;
	}

	public double getShoeCutoffPercentageNumerator()
	{
		return shoeCutoffPercentageNumerator;
	}

	public int getShoeDeckCount()
	{
		return shoeDeckCount;
	}

	public boolean isDealerTurnActive(EngineState currentState, Dealer dealer)
	{
		final int MINIMUM_SCORE_TO_STAND =
			getDealerHitsOnSoft17()
			&& dealer.getHand().getScore() == BlackjackConstants.DEFAULT_DEALER_MINIMUM_STAND_SCORE
			&& dealer.getHand().isSoft()
				? BlackjackConstants.DEFAULT_DEALER_MINIMUM_STAND_SCORE + 1
				: BlackjackConstants.DEFAULT_DEALER_MINIMUM_STAND_SCORE;
		return currentState == EngineState.DEALER_TURN
			&& dealer.getHand().getScore() < MINIMUM_SCORE_TO_STAND;
	}

	public boolean isDoubleDownPossible(HandContext activeHandContext,
										EngineState currentState, Player player)
	{
		return currentState == EngineState.PLAYER_TURN
			&& !activeHandContext.isAltered()
			&& (activeHandContext.getType() == HandContextType.MAIN
				|| isDoublingDownOnSplitHandsAllowed())
			&& player.getChips().compareTo(activeHandContext.getBet().getAmount()) >= 0;
	}

	public boolean isDoublingDownOnSplitHandsAllowed()
	{
		return doublingDownOnSplitHandsAllowed;
	}

	public boolean isInsuranceBetPossible(HandContext activeHandContext, EngineState currentState,
									  	Player player, Hand dealerHand)
	{
		return (currentState == EngineState.DEALING || currentState == EngineState.INSURANCE_CHECK)
			&& !activeHandContext.isAltered()
			&& player.getChips().compareTo(activeHandContext.getBet().getHalf()) >= 0
			&& dealerHand.getCards().getFirst().getRank() == Rank.ACE
			&& player.getContexts().size() == 1;
	}

	public boolean isLoggingEnabled()
	{
		return loggingEnabled;
	}

	public boolean isSplitPossible(HandContext activeHandContext, EngineState currentState,
								   int activeHandContextIndex, Player player)
	{
		return currentState == EngineState.PLAYER_TURN
			&& !activeHandContext.isAltered()
			&& activeHandContext.getHand().isPocketPair()
			&& activeHandContextIndex < getMaximumSplitCount()
			&& player.getChips().compareTo(activeHandContext.getBet().getAmount()) >= 0;
	}

	public boolean isSurrenderingAllowed()
	{
		return surrenderingAllowed;
	}

	public boolean isSurrenderingOnSplitHandsAllowed()
	{
		return surrenderingOnSplitHandsAllowed;
	}

	public boolean isSurrenderingPossible(HandContext activeHandContext,
										  EngineState currentState)
	{
		return currentState == EngineState.PLAYER_TURN
			&& isSurrenderingAllowed()
			&& !activeHandContext.isAltered()
			&& (activeHandContext.getType() == HandContextType.MAIN
				|| isSurrenderingOnSplitHandsAllowed());
	}

	public void setDealerHitsOnSoft17(boolean value)
	{
		dealerHitsOnSoft17 = value;
	}


	public void setDoublingDownOnSplitHandsAllowed(boolean value)
	{
		doublingDownOnSplitHandsAllowed = value;
	}

	public void setMaximumSplitCount(int maximumSplitCount)
	{
		InputManager.validateNumber(
			maximumSplitCount,
			"maximumSplitCount",
			0,
			Float.MAX_VALUE
		);

		this.maximumSplitCount = maximumSplitCount;
	}

	public void setMinimumBetAmount(BigDecimal minimumBetAmount)
	{
		assert minimumBetAmount.compareTo(BigDecimal.ZERO) > 0;
		this.minimumBetAmount = minimumBetAmount;
	}

	public void setPlayerInitialChips(BigDecimal playerInitialChips)
	{
		assert playerInitialChips != null && playerInitialChips.compareTo(BigDecimal.ZERO) > 0
			: "playerInitialChips == null || playerInitialChips.compareTo(BigDecimal.ZERO) <= 0";
		this.playerInitialChips = playerInitialChips;
	}

	public void setShoeCutoffPercentageNumerator(double shoeCutoffPercentageNumerator)
	{
		InputManager.validateNumber(
			shoeCutoffPercentageNumerator,
			"shoeCutoffPercentageNumerator",
			Shoe.MINIMUM_CUTOFF_PERCENTAGE_NUMERATOR,
			Shoe.MAXIMUM_CUTOFF_PERCENTAGE_NUMERATOR
		);

		this.shoeCutoffPercentageNumerator = shoeCutoffPercentageNumerator;
	}

	public void setShoeDeckCount(int shoeDeckCount)
	{
		InputManager.validateNumber(
			shoeDeckCount,
			"shoeDeckCount",
			1,
			Float.MAX_VALUE
		);

		this.shoeDeckCount = shoeDeckCount;
	}

	public void setSurrenderingAllowed(boolean value)
	{
		surrenderingAllowed = value;
	}

	public void setSurrenderingOnSplitHandsAllowed(boolean value)
	{
		surrenderingOnSplitHandsAllowed = value;
	}

	@Override
	public String toString()
	{
		return String.format(
			"%s[dealerHitsOnSoft17=%s,doublingDownOnSplitHandsAllowed=%s,loggingEnabled=%s,maximumSplitCount=%d,minimumBetAmount=%s,playerInitialChips=%s,shoeCutoffPercentageNumerator=%.2f,shoeDeckCount=%d,surrenderingAllowed=%s,surrenderingOnSplitHandsAllowed=%s]",
			getClass().getName(),
			getDealerHitsOnSoft17(),
			isDoublingDownOnSplitHandsAllowed(),
			isLoggingEnabled(),
			getMaximumSplitCount(),
			getMinimumBetAmount(),
			getPlayerInitialChips(),
			getShoeCutoffPercentageNumerator(),
			getShoeDeckCount(),
			isSurrenderingAllowed(),
			isSurrenderingOnSplitHandsAllowed()
		);
	}
}