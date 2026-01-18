package com.github.kqfall1.java.blackjackEngine.model.engine;

import com.github.kqfall1.java.blackjackEngine.model.betting.PayoutRatio;
import java.math.BigDecimal;

/**
 * A static class that holds values corresponding to standard, universal blackjack truths.
 *
 * @author kqfall1
 * @since 03/01/2026
 */
public final class BlackjackConstants
{
	public static final int ACE_HIGH_VALUE = 11;
	public static final int ACE_LOW_VALUE = 1;
	public static final int ACE_VALUE_DIFFERENTIAL = ACE_HIGH_VALUE - ACE_LOW_VALUE;
	public static final int DEFAULT_CHIP_SCALE = 3;
	public static final int DEFAULT_DEALER_MINIMUM_STAND_SCORE = 17;
	public static final int DEFAULT_FULL_DECK_CARD_COUNT = 52;
	public static final int DEFAULT_MAXIMUM_SPLIT_COUNT = 1;
	public static final BigDecimal DEFAULT_MINIMUM_BET_AMOUNT = BigDecimal.ONE;
	public static final int DEFAULT_SHOE_DECK_COUNT = 8;
	public static final double DEFAULT_SHOE_PENETRATION = 80.0;
	public static final int DEFAULT_TOP_SCORE = 21;
	public static final int INITIAL_CARD_COUNT = 2;
	public static final int INITIAL_HAND_COUNT = 1;

	public static final PayoutRatio BLACKJACK_RATIO = new PayoutRatio(
		BigDecimal.valueOf(3),
		BigDecimal.TWO
	);
	public static final String BLACKJACK_RATIO_KEY = "Blackjack Payout Ratio";
	public static final PayoutRatio INSURANCE_RATIO = new PayoutRatio(
		BigDecimal.TWO,
		BigDecimal.ONE
	);
	public static final String INSURANCE_RATIO_KEY = "Insurance Payout Ratio";
	public static final PayoutRatio PUSH_RATIO = new PayoutRatio(
		BigDecimal.ONE,
		BigDecimal.TWO
	);
	public static final String PUSH_RATIO_KEY = "Push Payout Ratio";
	public static final PayoutRatio SURRENDER_RATIO = new PayoutRatio(
		BigDecimal.ONE,
		BigDecimal.valueOf(4)
	);
	public static final String SURRENDER_RATIO_KEY = "Surrender Payout Ratio";

	private BlackjackConstants() {}
}