package com.github.kqfall1.java.blackjackEngine.model.engine;

import com.github.kqfall1.java.blackjackEngine.model.cards.Shoe;
import com.github.kqfall1.java.managers.InputManager;
import java.math.BigDecimal;

/**
 * A configuration object to be encapsulated by a {@code BlackjackRuleset} to determine valid
 * game actions.
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
	private double shoeCutoffPercentageNumerator;
	private int shoeDeckCount;
	private boolean shouldDealerHitOnSoft17;
	private boolean splittingAcesAllowed;
	private boolean surrenderingAllowed;
	private boolean surrenderingOnSplitHandsAllowed;

	public BlackjackRulesetConfiguration()
	{
		setMaximumSplitCount(BlackjackConstants.DEFAULT_MAXIMUM_SPLIT_COUNT);
		setMinimumBetAmount(BlackjackConstants.DEFAULT_MINIMUM_BET_AMOUNT);
		setShoeCutoffPercentageNumerator(BlackjackConstants.DEFAULT_SHOE_CUTOFF_PERCENTAGE_NUMERATOR);
		setShoeDeckCount(BlackjackConstants.DEFAULT_SHOE_DECK_COUNT);
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
			"%s[doublingDownOnSplitHandsAllowed=%s,loggingEnabled=%s,maximumSplitCount=%d,minimumBetAmount=%s,playerInitialChips=%s,shoeCutoffPercentageNumerator=%.2f,shoeDeckCount=%d,shouldDealerHitOnSoft17=%s,splittingAcesAllowed=%s,surrenderingAllowed=%s,surrenderingOnSplitHandsAllowed=%s]",
			getClass().getName(),
			isDoublingDownOnSplitHandsAllowed(),
			isLoggingEnabled(),
			getMaximumSplitCount(),
			getMinimumBetAmount(),
			getPlayerInitialChips(),
			getShoeCutoffPercentageNumerator(),
			getShoeDeckCount(),
			getShouldDealerHitOnSoft17(),
			isSplittingAcesAllowed(),
			isSurrenderingAllowed(),
			isSurrenderingOnSplitHandsAllowed()
		);
	}
}