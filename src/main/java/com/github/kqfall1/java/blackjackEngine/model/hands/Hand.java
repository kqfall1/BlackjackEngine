package com.github.kqfall1.java.blackjackEngine.model.hands;

import com.github.kqfall1.java.blackjackEngine.model.engine.RuleConfig;
import com.github.kqfall1.java.blackjackEngine.model.cards.Card;
import com.github.kqfall1.java.blackjackEngine.model.cards.Rank;
import java.util.*;

/**
 * A collection of {@code Card} objects, held by a {@code Dealer} or {@code Player}
 * in a blackjack betting round.
 *
 * @author kqfall1
 * @since 14/12/2025
 */
public final class Hand
{
	private final List<Card> cards;

	public Hand()
	{
		cards = new ArrayList<>();
	}

	public void addCard(Card card)
	{
		assert card != null && !getCards().contains(card)
			: "card == null || cards.contains(card)";
		cards.add(card);
	}

	@Override
	public boolean equals(Object otherObject)
	{
		if (this == otherObject)
		{
			return true;
		}
		else if (otherObject == null || getClass() != otherObject.getClass())
		{
			return false;
		}

		final var otherHand = (Hand) otherObject;
		return getCards().equals(otherHand.getCards());
	}

	public int getAceCount()
	{
		int aceCount = 0;

		for (Card card : getCards())
		{
			if (card.getRank() == Rank.ACE)
			{
				aceCount++;
			}
		}

		return aceCount;
	}

	public List<Card> getCards()
	{
		return List.copyOf(cards);
	}

	/**
 	* Sums the values of all {@code Card} objects in {@code cards} using
	 * {@code Card.ACE_LOW_VALUE}.
 	*/
	public int getLowScore()
	{
		int lowScore = 0;

		for (Card card : getCards())
		{
			lowScore += switch (card.getRank())
			{
				case Rank.TWO -> 2;
				case Rank.THREE -> 3;
				case Rank.FOUR -> 4;
				case Rank.FIVE -> 5;
				case Rank.SIX -> 6;
				case Rank.SEVEN -> 7;
				case Rank.EIGHT -> 8;
				case Rank.NINE -> 9;
				case Rank.TEN, Rank.JACK, Rank.QUEEN, Rank.KING -> 10;
				case Rank.ACE -> RuleConfig.ACE_LOW_VALUE;
			};
		}

		return lowScore;
	}

	public int getScore()
	{
		int score = getLowScore();

		for (int count = 0; count < getAceCount(); count++)
		{
			if (score + RuleConfig.ACE_VALUE_DIFFERENTIAL
				<= RuleConfig.TOP_SCORE)
			{
				score += RuleConfig.ACE_VALUE_DIFFERENTIAL;
			}
			else
			{
				break;
			}
		}

		return score;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(getCards());
	}

	/**
 	 * Determines whether {@code cards} contains a {@code Card} of {@code Rank.ACE}
	 * in which its individual contribution to the hand score equals only 1 point.
	 * @return {@code true} if this {@code Hand} contains a low ace, {@code false} otherwise.
 	 */
	public boolean hasLowAce()
	{
		return getScore() != getLowScore();
	}

	public boolean isBlackjack()
	{
		return getCards().size() == RuleConfig.INITIAL_CARD_COUNT
			&& getScore() == RuleConfig.TOP_SCORE;
	}

	public boolean isBusted()
	{
		return getScore() > RuleConfig.TOP_SCORE;
	}

	public boolean isPocketPair()
	{
		if (getCards().size() != RuleConfig.INITIAL_CARD_COUNT)
		{
			return false;
		}

		final var iterator = getCards().iterator();
		final var firstCard = iterator.next();
		final var secondCard = iterator.next();
		return firstCard.getRank() == secondCard.getRank();
	}

	public void removeCard(int cardIndex)
	{
		assert cardIndex >= 0 && cardIndex < getCards().size()
			: "cardIndex < 0 && cardIndex >= getCards().size()";
		cards.remove(cardIndex);
	}

	@Override
	public String toString()
	{
		return String.format(
			"%s[cards=%s]",
			getClass().getName(),
			getCards()
		);
	}
}