package com.github.kqfall1.java.blackjackEngine.model.entities;

import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import com.github.kqfall1.java.blackjackEngine.model.hands.PlayerHand;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Places {@code Bet} objects and plays before the {@code Dealer} in a
 * blackjack betting round.
 *
 * @author kqfall1
 * @since 14/12/2025
 */
public final class Player
{
	private BigDecimal chips;
	private List<PlayerHand> hands;

	public Player()
	{
		setChips(BigDecimal.ZERO);
		setHands(new ArrayList<>());
	}

	public void addHand(PlayerHand hand)
	{
		assert hand != null && !hands.contains(hand)
			: "hand == null || hands.contains(hand)";
		hands.add(hand);
	}

	public BigDecimal getChips()
	{
		return chips;
	}

	public List<PlayerHand> getHands()
	{
		return List.copyOf(hands);
	}

	public void setChips(BigDecimal chips)
	{
		assert chips != null : "chips == null";

		if (chips.compareTo(BigDecimal.ZERO) < 0)
		{
			throw new InsufficientChipsException(this, chips);
		}

		this.chips = chips;
	}

	public void setHands(List<PlayerHand> hands)
	{
		assert hands != null : "cards == null";
		this.hands = hands;
	}

	@Override
	public String toString()
	{
		return String.format(
			"%s[chips=%s,hands=%s]",
			getClass().getName(),
			getChips(),
			getHands()
		);
	}
}