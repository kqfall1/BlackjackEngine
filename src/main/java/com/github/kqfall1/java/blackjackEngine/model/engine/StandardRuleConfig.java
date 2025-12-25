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
	public static final int DEALER_MINIMUM_STAND_SCORE = 17;
	public static final int DEFAULT_MAXIMUM_SPLIT_COUNT = 1;
	public static final int INITIAL_CARD_COUNT = 2;
	public static final int INITIAL_HAND_COUNT = 1;
	public static final int TOP_SCORE = 21;

	private boolean dealerHitsOnSoft17;
	private boolean loggingEnabled;
	private int maximumSplitCount;
	private boolean playerCanDoubleDownOnSplitHands;
	private boolean playerCanSurrenderOnSplitHands;
	private BigDecimal playerInitialChips;

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
	}

	public StandardRuleConfig(int maximumSplitCount)
	{
		setMaximumSplitCount(maximumSplitCount);
	}

	public boolean getDealerHitsOnSoft17()
	{
		return dealerHitsOnSoft17;
	}

	public boolean getLoggingEnabled()
	{
		return loggingEnabled;
	}

	public int getMaximumSplitCount()
	{
		return maximumSplitCount;
	}

	public boolean getPlayerCanDoubleDownOnSplitHands()
	{
		return playerCanDoubleDownOnSplitHands;
	}

	public boolean getPlayerCanSurrenderOnSplitHands()
	{
		return playerCanSurrenderOnSplitHands;
	}

	public BigDecimal getPlayerInitialChips()
	{
		return playerInitialChips;
	}

	public boolean isDealerTurnActive(EngineState currentState, Dealer dealer)
	{
		final int MINIMUM_SCORE_TO_STAND = getDealerHitsOnSoft17()
			? DEALER_MINIMUM_STAND_SCORE + 1
			: DEALER_MINIMUM_STAND_SCORE;
		return currentState == EngineState.DEALER_TURN
			&& dealer.getHand().getScore() < MINIMUM_SCORE_TO_STAND;
	}

	public boolean isGameActive(Player player)
	{
		return player.getChips().compareTo(BigDecimal.ZERO) > 0;
	}

	public boolean isInsuranceBetPossible(HandContext activeHandContext, Player player,
										  Hand dealerHand)
	{
		return !activeHandContext.isAltered()
			&& player.getChips().compareTo(activeHandContext.getBet().getHalf()) >= 0
			&& dealerHand.getCards().getLast().getRank() == Rank.ACE
			&& player.getContexts().size() == 1;
	}

	public boolean isDoubleDownPossible(HandContext activeHandContext, Player player)
	{
		return !activeHandContext.isAltered()
			&& (activeHandContext.getType() == HandContextType.MAIN
				|| getPlayerCanDoubleDownOnSplitHands())
			&& player.getChips().compareTo(activeHandContext.getBet().getAmount()) >= 0;
	}

	public boolean isSplitPossible(HandContext activeHandContext, int activeHandContextIndex,
								   Player player)
	{
		return !activeHandContext.isAltered()
			&& activeHandContext.getHand().isPocketPair()
			&& activeHandContextIndex < getMaximumSplitCount()
			&& player.getChips().compareTo(activeHandContext.getBet().getAmount()) >= 0;
	}

	public boolean isSurrenderPossible(HandContext activeHandContext, Player player)
	{
		return !activeHandContext.isAltered()
			&& (activeHandContext.getType() == HandContextType.MAIN
				|| getPlayerCanSurrenderOnSplitHands());
	}

	public void setDealerHitsOnSoft17(boolean value)
{
dealerHitsOnSoft17 = value;
}

	public void setLoggingEnabled(boolean value)
	{
		loggingEnabled = value;
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

	public void setPlayerCanDoubleDownOnSplitHands(boolean value)
	{
		playerCanDoubleDownOnSplitHands = value;
	}

	public void setPlayerCanSurrenderOnSplitHands(boolean value)
	{
		playerCanSurrenderOnSplitHands = value;
	}

	public void setPlayerInitialChips(BigDecimal playerInitialChips)
	{
		assert playerInitialChips != null && playerInitialChips.compareTo(BigDecimal.ZERO) > 0
			: "playerInitialChips == null || playerInitialChips.compareTo(BigDecimal.ZERO) <= 0";
		this.playerInitialChips = playerInitialChips;
	}

	@Override
	public String toString()
	{
		return String.format(
			"%s[dealerHitsOnSoft17=%s,maximumSplitCount=%s,playerCanDoubleDownOnSplitHands=%s,playerCanSurrenderOnSplitHands=%s]",
			getClass().getName(),
			getDealerHitsOnSoft17(),
			getMaximumSplitCount(),
			getPlayerCanDoubleDownOnSplitHands(),
			getPlayerCanSurrenderOnSplitHands()
		);
	}
}