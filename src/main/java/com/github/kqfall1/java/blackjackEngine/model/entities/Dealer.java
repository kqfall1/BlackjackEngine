package com.github.kqfall1.java.blackjackEngine.model.entities;

import com.github.kqfall1.java.blackjackEngine.model.cards.Card;
import com.github.kqfall1.java.blackjackEngine.model.cards.Deck;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.NoMoreCardsException;
import com.github.kqfall1.java.blackjackEngine.model.hands.Hand;
import com.github.kqfall1.java.blackjackEngine.model.interfaces.Drawable;

/**
 * Controls a {@code Drawable} and a {@code Hand}. Draws {@code Card} objects
 * one at a time.
 *
 * <p>
 * The encapsulated {@code Hand} is played in a mechanical manner after all
 * {@code Player} {@code HandContext} objects are finalized, according to rules
 * defined in {@code RuleConfig}.
 * </p>
 */
public final class Dealer
{
	private Drawable cardSource;
	private Hand hand;

	public Dealer()
	{
		setCardSource(new Deck());
		setHand(new Hand());
	}

	public Drawable getCardSource()
	{
		return cardSource;
	}

	public Hand getHand()
	{
		return hand;
	}

	public Card hit() throws NoMoreCardsException
	{
		return getCardSource().draw();
	}

	public void setCardSource(Drawable cardSource)
	{
		assert cardSource != null : "deck == null";
		this.cardSource = cardSource;
	}

	public void setHand(Hand hand)
	{
		assert hand != null : "hand == null";
		this.hand = hand;
	}

	@Override
	public String toString()
	{
		return String.format(
			"%s[cardSource=%s,hand=%s]",
			getClass().getName(),
			getCardSource(),
			getHand()
		);
	}
}