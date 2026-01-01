package com.github.kqfall1.java.blackjackEngine.model.engine;

import com.github.kqfall1.java.blackjackEngine.model.betting.PayoutRatio;
import com.github.kqfall1.java.blackjackEngine.model.cards.Rank;
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
	public static final int ACE_HIGH_VALUE = 11;
	public static final int ACE_LOW_VALUE = 1;
	public static final int ACE_VALUE_DIFFERENTIAL = ACE_HIGH_VALUE - ACE_LOW_VALUE;
	public static final int CHIP_SCALE = 3;
	public static final int DEALER_MINIMUM_STAND_SCORE = 17;
	public static final int DEFAULT_MAXIMUM_SPLIT_COUNT = 1;
	public static final BigDecimal DEFAULT_MINIMUM_BET_AMOUNT = BigDecimal.ONE;
	public static final int FULL_DECK_CARD_COUNT = 52;
	public static final int INITIAL_CARD_COUNT = 2;
	public static final int INITIAL_HAND_COUNT = 1;
	public static final int TOP_SCORE = 21;

	private boolean dealerHitsOnSoft17;
	private boolean doublingDownOnSplitHandsAllowed;
	private boolean loggingEnabled;
	private int maximumSplitCount;
	private BigDecimal minimumBetAmount;
	private BigDecimal playerInitialChips;
	private boolean surrenderingAllowed;
	private boolean surrenderingOnSplitHandsAllowed;

	public static final PayoutRatio BLACKJACK = new PayoutRatio(
		BigDecimal.valueOf(3),
		BigDecimal.TWO
	);
	public static final PayoutRatio INSURANCE = new PayoutRatio(
		BigDecimal.TWO,
		BigDecimal.ONE
	);
	public static final PayoutRatio PUSH = new PayoutRatio(
		BigDecimal.ONE,
		BigDecimal.TWO
	);
	public static final PayoutRatio SURRENDER = new PayoutRatio(
		BigDecimal.ONE,
		BigDecimal.valueOf(4)
	);

	public StandardRuleConfig()
	{
		setMaximumSplitCount(DEFAULT_MAXIMUM_SPLIT_COUNT);
		setMinimumBetAmount(DEFAULT_MINIMUM_BET_AMOUNT);
	}

	public StandardRuleConfig(int maximumSplitCount, BigDecimal minimumBetAmount)
	{
		setMaximumSplitCount(maximumSplitCount);
		setMinimumBetAmount(minimumBetAmount);
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

	public boolean isDealerTurnActive(EngineState currentState, Dealer dealer)
	{
		final int MINIMUM_SCORE_TO_STAND =
			getDealerHitsOnSoft17()
			&& dealer.getHand().getScore() == DEALER_MINIMUM_STAND_SCORE
			&& dealer.getHand().isSoft()
				? DEALER_MINIMUM_STAND_SCORE + 1
				: DEALER_MINIMUM_STAND_SCORE;
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
			"%s[dealerHitsOnSoft17=%s,doublingDownOnSplitHandsAllowed=%s,loggingEnabled=%s,maximumSplitCount=%s,minimumBetAmount=%s,playerInitialChips=%s,surrenderingAllowed=%s,surrenderingOnSplitHandsAllowed=%s]",
			getClass().getName(),
			getDealerHitsOnSoft17(),
			isDoublingDownOnSplitHandsAllowed(),
			isLoggingEnabled(),
			getMaximumSplitCount(),
			getMinimumBetAmount(),
			getPlayerInitialChips(),
			isSurrenderingAllowed(),
			isSurrenderingOnSplitHandsAllowed()
		);
	}
}