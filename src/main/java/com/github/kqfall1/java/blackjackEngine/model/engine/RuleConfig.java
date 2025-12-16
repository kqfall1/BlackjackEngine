package com.github.kqfall1.java.blackjackEngine.model.engine;

import com.github.kqfall1.java.blackjackEngine.model.betting.PayoutRatio;
import java.math.BigDecimal;

/**
 * A configuration object to be used by a {@code GameEngine} to make gameplay
 * decisions and satisfy end users by providing customization.
 *
 * @author kqfall1
 * @since 14/12/2025
 */
public final class RuleConfig
{
	public static final int ACE_HIGH_VALUE = 11;
	public static final int ACE_LOW_VALUE = 1;
	public static final int ACE_VALUE_DIFFERENTIAL =
		ACE_HIGH_VALUE - ACE_LOW_VALUE;
	private boolean dealerHitsOnSoft17;
	public static final int DEALER_MINIMUM_STAND_SCORE = 17;
	public static final int INITIAL_CARD_COUNT = 2;
	public static final int INITIAL_HAND_COUNT = 1;
	public static final int MAXIMUM_PLAYER_HANDS_PER_BETTING_ROUND = 2;
	private boolean playerCanDoubleDownOnSplitHands;
	private boolean playerCanSurrenderOnSplitHands;
	public static final int TOP_SCORE = 21;

	public static final PayoutRatio BLACKJACK = new PayoutRatio(
		BigDecimal.valueOf(3), BigDecimal.TWO
	);
	public static final PayoutRatio INSURANCE = new PayoutRatio(
		BigDecimal.TWO, BigDecimal.ONE
	);
	public static final PayoutRatio PUSH = new PayoutRatio(
		BigDecimal.ONE, BigDecimal.TWO
	);
	public static final PayoutRatio SURRENDER = new PayoutRatio(
		BigDecimal.ONE, BigDecimal.valueOf(4)
	);

	public boolean getDealerHitsOnSoft17()
	{
		return dealerHitsOnSoft17;
	}

	public boolean getPlayerCanDoubleDownOnSplitHands()
	{
		return playerCanDoubleDownOnSplitHands;
	}

	public boolean getPlayerCanSurrenderOnSplitHands()
	{
		return playerCanSurrenderOnSplitHands;
	}

	public void setDealerHitsOnSoft17(boolean value)
	{
		dealerHitsOnSoft17 = value;
	}

	public void setPlayerCanDoubleDownOnSplitHands(boolean value)
	{
		playerCanDoubleDownOnSplitHands = value;
	}

	public void setPlayerCanSurrenderOnSplitHands(boolean value)
	{
		playerCanSurrenderOnSplitHands = value;
	}
}