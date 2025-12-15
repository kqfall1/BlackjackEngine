package com.github.kqfall1.java.blackjackEngine.model.entities;

import com.github.kqfall1.java.blackjackEngine.controllers.RuleConfig;
import com.github.kqfall1.java.blackjackEngine.model.cards.Deck;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.EmptyDeckException;
import com.github.kqfall1.java.blackjackEngine.model.hands.Hand;
import com.github.kqfall1.java.blackjackEngine.model.hands.PlayerHand;
import java.util.NoSuchElementException;

/**
 * Controls the {@code Deck} and a {@code Hand}.
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

	public void deal(PlayerHand playerHand)
	{
		hit(playerHand.getHand());
		hit(getHand());
		hit(playerHand.getHand());
		hit(getHand());
	}

	public Deck getDeck()
	{
		return deck;
	}

	public Hand getHand()
	{
		return hand;
	}

//	public boolean getMustHit()
//	{
//		return getHand().getScore() < RuleConfig.DEALER_MINIMUM_STAND_SCORE
//			|| (getHand().getScore() == RuleConfig.DEALER_MINIMUM_STAND_SCORE
//				&& getHand().hasLowAce());
//	}

	public void hit(Hand hand)
	{
		try
		{
			hand.addCard(getDeck().draw());
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