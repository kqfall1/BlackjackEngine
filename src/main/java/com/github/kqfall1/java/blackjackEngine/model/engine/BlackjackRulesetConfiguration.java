package com.github.kqfall1.java.blackjackEngine.model.engine;

import com.github.kqfall1.java.blackjackEngine.model.cards.Shoe;
import com.github.kqfall1.java.managers.InputManager;
import java.math.BigDecimal;

/**
 * A configuration object to be encapsulated by a {@code BlackjackRuleset} to determine valid game actions.
 *
 * @author kqfall1
 * @since 14/12/2025
 */
public final class BlackjackRulesetConfiguration
{
	private boolean doublingDownOnSplitHandsAllowed;
	private boolean loggingEnabled;
	private int maximumSplitCount;
	private BigDecimal minimumBetAmount;
	private BigDecimal playerInitialChips;
	private int shoeDeckCount;
	private double shoePenetration;
	private boolean shouldDealerHitOnSoft17;
	private boolean splittingAcesAllowed;
	private boolean surrenderingAllowed;
	private boolean surrenderingOnSplitHandsAllowed;

	public BlackjackRulesetConfiguration()
	{
		setMaximumSplitCount(BlackjackConstants.DEFAULT_MAXIMUM_SPLIT_COUNT);
		setMinimumBetAmount(BlackjackConstants.DEFAULT_MINIMUM_BET_AMOUNT);
		setShoeDeckCount(BlackjackConstants.DEFAULT_SHOE_DECK_COUNT);
		setShoePenetration(BlackjackConstants.DEFAULT_SHOE_PENETRATION);
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

	public int getShoeDeckCount()
	{
		return shoeDeckCount;
	}

	public double getShoePenetration()
	{
		return shoePenetration;
	}

	public boolean getShouldDealerHitOnSoft17()
	{
		return shouldDealerHitOnSoft17;
	}

	public boolean isDoublingDownOnSplitHandsAllowed()
	{
		return doublingDownOnSplitHandsAllowed;
	}

	public boolean isLoggingEnabled()
	{
		return loggingEnabled;
	}

	public boolean isSplittingAcesAllowed()
	{
		return splittingAcesAllowed;
	}

	public boolean isSurrenderingAllowed()
	{
		return surrenderingAllowed;
	}

	public boolean isSurrenderingOnSplitHandsAllowed()
	{
		return surrenderingOnSplitHandsAllowed;
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

	public void setLoggingEnabled(boolean value)
	{
		loggingEnabled = value;
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

	public void setShoeDeckCount(int shoeDeckCount)
	{
		InputManager.validateNumber(
			shoeDeckCount,
			"shoeDeckCount",
			Shoe.MINIMUM_NUMBER_OF_DECKS,
			Shoe.MAXIMUM_NUMBER_OF_DECKS
		);

		this.shoeDeckCount = shoeDeckCount;
	}

	public void setShoePenetration(double shoePenetration)
	{
		InputManager.validateNumber(
			shoePenetration,
			"shoePenetration",
			Shoe.MINIMUM_PENETRATION,
			Shoe.MAXIMUM_PENETRATION
		);

		this.shoePenetration = shoePenetration;
	}

	public void setShouldDealerHitOnSoft17(boolean value)
	{
		shouldDealerHitOnSoft17 = value;
	}

	public void setSplittingAcesAllowed(boolean value)
	{
		splittingAcesAllowed = value;
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
			"%s[doublingDownOnSplitHandsAllowed=%s,loggingEnabled=%s,maximumSplitCount=%d,minimumBetAmount=%s,playerInitialChips=%s,shoeDeckCount=%d,shoePenetration=%.2f,shouldDealerHitOnSoft17=%s,splittingAcesAllowed=%s,surrenderingAllowed=%s,surrenderingOnSplitHandsAllowed=%s]",
			getClass().getName(),
			isDoublingDownOnSplitHandsAllowed(),
			isLoggingEnabled(),
			getMaximumSplitCount(),
			getMinimumBetAmount(),
			getPlayerInitialChips(),
			getShoeDeckCount(),
			getShoePenetration(),
			getShouldDealerHitOnSoft17(),
			isSplittingAcesAllowed(),
			isSurrenderingAllowed(),
			isSurrenderingOnSplitHandsAllowed()
		);
	}
}