package com.github.kqfall1.java.blackjackEngine.model.hands;

import com.github.kqfall1.java.blackjackEngine.model.engine.StandardRuleConfig;
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
		return Objects.equals(getCards(), otherHand.getCards());
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
				case TWO -> 2;
				case THREE -> 3;
				case FOUR -> 4;
				case FIVE -> 5;
				case SIX -> 6;
				case SEVEN -> 7;
				case EIGHT -> 8;
				case NINE -> 9;
				case TEN, JACK, QUEEN, KING -> 10;
				case ACE -> StandardRuleConfig.ACE_LOW_VALUE;
			};
		}

		return lowScore;
	}

	public int getScore()
	{
		var score = getLowScore();

		for (int count = 0; count < getAceCount(); count++)
		{
			if (score + StandardRuleConfig.ACE_VALUE_DIFFERENTIAL
				<= StandardRuleConfig.TOP_SCORE)
			{
				score += StandardRuleConfig.ACE_VALUE_DIFFERENTIAL;
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

	public boolean isBlackjack()
	{
		return getCards().size() == StandardRuleConfig.INITIAL_CARD_COUNT
			&& getScore() == StandardRuleConfig.TOP_SCORE;
	}

	public boolean isBusted()
	{
		return getScore() > StandardRuleConfig.TOP_SCORE;
	}

	public boolean isPocketPair()
	{
		if (getCards().size() != StandardRuleConfig.INITIAL_CARD_COUNT)
		{
			return false;
		}

		return getCards().getFirst().getRank().equals(
			getCards().getLast().getRank()
		);
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