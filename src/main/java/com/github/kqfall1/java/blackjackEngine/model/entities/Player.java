package com.github.kqfall1.java.blackjackEngine.model.entities;

import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import com.github.kqfall1.java.blackjackEngine.model.hands.HandContext;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * An actor that places {@code Bet} objects and plays at least one
 * {@code Hand} before the {@code Dealer} in a blackjack betting round.
 *
 * @author kqfall1
 * @since 14/12/2025
 */
public final class Player
{
	private BigDecimal chips;
	private final List<HandContext> contexts;

	public Player() throws InsufficientChipsException
	{
		setChips(BigDecimal.ZERO);
		contexts = new ArrayList<>();
	}

	public void addContext(HandContext context)
	{
		assert context != null && !getContexts().contains(context)
			: "context == null || getContexts().contains(context)";
		contexts.add(context);
	}

	public void clearContexts()
	{
		contexts.clear();
	}

	public BigDecimal getChips()
	{
		return chips;
	}

	public List<HandContext> getContexts()
	{
		return List.copyOf(contexts);
	}

	public void removeContext(int contextIndex)
	{
		assert contextIndex >= 0 && contextIndex < contexts.size();
		contexts.remove(contextIndex);
	}

	public void setChips(BigDecimal chips) throws InsufficientChipsException
	{
		assert chips != null : "chips == null";

		if (chips.compareTo(BigDecimal.ZERO) < 0)
		{
			throw new InsufficientChipsException(this, chips);
		}

		this.chips = chips;
	}

	@Override
	public String toString()
	{
		return String.format(
			"%s[chips=%s,contexts=%s]",
			getClass().getName(),
			getChips(),
			getContexts()
		);
	}
}