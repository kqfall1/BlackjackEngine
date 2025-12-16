package com.github.kqfall1.java.blackjackEngine.model.entities;

import com.github.kqfall1.java.blackjackEngine.model.cards.Card;
import com.github.kqfall1.java.blackjackEngine.model.cards.Deck;
import com.github.kqfall1.java.blackjackEngine.model.hands.Hand;

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
		var card = getDeck().draw();

		if (card == null)
		{
			setDeck(new Deck());
			card = hit();
		}

		return card;
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