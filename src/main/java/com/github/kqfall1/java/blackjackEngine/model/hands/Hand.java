package com.github.kqfall1.java.blackjackEngine.model.hands;

import com.github.kqfall1.java.blackjackEngine.model.engine.BlackjackConstants;
import com.github.kqfall1.java.blackjackEngine.model.cards.Card;
import com.github.kqfall1.java.blackjackEngine.model.enums.Rank;
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

	public void addCards(Card... cards)
	{
		assert cards != null && cards.length > 0 : "cards == null || cards.length <= 0";

		for (Card card : cards)
		{
			assert card != null: "card == null";
			this.cards.add(card);
		}
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
		return Objects.equals(getCards(), otherHand.getCards());
	}

	private int getAceCount()
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
	private int getLowScore()
	{
		int lowScore = 0;

		for (Card card : getCards())
		{
			lowScore += switch (card.getRank())
			{
				case TWO -> 2;
				case THREE -> 3;
				case FOUR -> 4;
				case FIVE -> 5;
				case SIX -> 6;
				case SEVEN -> 7;
				case EIGHT -> 8;
				case NINE -> 9;
				case TEN, JACK, QUEEN, KING -> 10;
				case ACE -> BlackjackConstants.ACE_LOW_VALUE;
			};
		}

		return lowScore;
	}

	public int getScore()
	{
		var score = getLowScore();

		for (int count = 0; count < getAceCount(); count++)
		{
			if (score + BlackjackConstants.ACE_VALUE_DIFFERENTIAL
				<= BlackjackConstants.DEFAULT_TOP_SCORE)
			{
				score += BlackjackConstants.ACE_VALUE_DIFFERENTIAL;
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

	public boolean isPocketPair()
	{
		if (getCards().size() != BlackjackConstants.INITIAL_CARD_COUNT)
		{
			return false;
		}

		return getCards().getFirst().getRank().equals(
			getCards().getLast().getRank()
		);
	}

	public boolean isSoft()
	{
		return getAceCount() > 0 && getLowScore() != getScore();
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
			"%s[cards=%s,score=%d]",
			getClass().getName(),
			getCards(),
			getScore()
		);
	}

	public String toStringPretty()
	{
		final var builder = new StringBuilder();
		builder.append("[");
		for (Card card : getCards())
		{
			builder.append(String.format("%s,", card.toStringPretty()));
		}
		return builder.append("]").toString();
	}
}