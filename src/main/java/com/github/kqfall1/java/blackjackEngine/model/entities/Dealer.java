package com.github.kqfall1.java.blackjackEngine.model.entities;

import com.github.kqfall1.java.blackjackEngine.model.cards.Card;
import com.github.kqfall1.java.blackjackEngine.model.cards.Deck;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.EmptyDeckException;
import com.github.kqfall1.java.blackjackEngine.model.hands.Hand;
import java.util.NoSuchElementException;

/**
 * Controls the {@code Deck} and a {@code Hand}. Draws {@code Card} objects
 * one at a time.
 *
 * <p>
 * The encapsulated {@code Hand} is played in a mechanical manner after all
 * {@code Player} drawing rounds are finalized, according to rules defined in
 * {@code RuleConfig}.
 * </p>
 */
public final class Dealer
{
	private Deck deck;
	private Hand hand;

	public Dealer()
	{
		setDeck(new Deck());
		setHand(new Hand());
	}

	public Deck getDeck()
	{
		return deck;
	}

	public Hand getHand()
	{
		return hand;
	}

	public Card hit()
	{
		try
		{
			return getDeck().draw();
		}
		catch (NoSuchElementException e)
		{
			final var ex = new EmptyDeckException(this, getDeck());
			ex.initCause(e);
			throw ex;
		}
	}

	public void setDeck(Deck deck)
	{
		assert deck != null : "deck == null";
		this.deck = deck;
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
			"%s[deck=%s,hand=%s]",
			getClass().getName(),
			getDeck(),
			getHand()
		);
	}
}