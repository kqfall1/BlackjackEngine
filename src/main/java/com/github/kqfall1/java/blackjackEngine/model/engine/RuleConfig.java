package com.github.kqfall1.java.blackjackEngine.model.engine;

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
	public static final int TOP_SCORE = 21;

	public boolean getDealerHitsOnSoft17()
	{
		return dealerHitsOnSoft17;
	}

	public void setDealerHitsOnSoft17(boolean value)
	{
		dealerHitsOnSoft17 = value;
	}
}