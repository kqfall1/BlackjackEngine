package com.github.kqfall1.java.blackjackEngine.model.betting;

import com.github.kqfall1.java.blackjackEngine.model.cards.Card;
import com.github.kqfall1.java.blackjackEngine.model.cards.Deck;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.NoMoreCardsException;
import com.github.kqfall1.java.blackjackEngine.model.interfaces.Drawable;
import java.util.*;

/**
 * A container for randomly assorted collections of {@code Card} objects.
 *
 * @author kqfall1
 * @since 31/12/2025
 */
public class Shoe implements Drawable
{
	final Queue<Card> cards;

	/**
 	 * The amount of {@code Card} objects needed to be left in this {@code Shoe}
	 * object's {@code cards} for {@code shufflingRequired} to be set to {@code true}.
 	 */
	private final int cutoffAmount;

	/**
 	 * Expresses the smallest percentage of {@code Card} objects in this {@code Shoe}
	 * for {@code shufflingRequired} to remain {@code false}. It is a {@code double}
	 * and must be between {@code 10} and {@code 90} inclusive.
 	 */
	private final double cutoffPercentageNumerator;
	private final int numberOfDecks;
	public static final double ONE_HUNDRED = 100.0;
	public static final double MAXIMUM_CUTOFF_PERCENTAGE_NUMERATOR = 90.0;
	public static final double MINIMUM_CUTOFF_PERCENTAGE_NUMERATOR = 10.0;

	/**
 	 * Is used to determine whether the {@code Shoe} should be reused for another
	 * betting round in a blackjack game.
 	 */
	private boolean shufflingRequired;

	/**
 	 * An amount added to the return value of {getCutoffPercentage} of this
	 * {@code Shoe} to add variance to the values of different {@code Shoe}
	 * object's {@code cutoffAmount} values.
 	 */
	private static final double VARIANCE = 0.05;

	public Shoe(double cutoffPercentageNumerator, int numberOfDecks)
	{
		final List<Card> cardsList = new ArrayList<>();
		for (int count = 0; count < numberOfDecks; count++)
		{
			cardsList.addAll(new Deck().getCards());
		}
		Collections.shuffle(cardsList);
		cards = new ArrayDeque<>(cardsList);

		assert cutoffPercentageNumerator >= MINIMUM_CUTOFF_PERCENTAGE_NUMERATOR && cutoffPercentageNumerator <= MAXIMUM_CUTOFF_PERCENTAGE_NUMERATOR
			: "cutoffPercentageNumerator < MINIMUM_CUTOFF_PERCENTAGE || cutoffPercentageNumerator > MAXIMUM_CUTOFF_PERCENTAGE";
		this.cutoffPercentageNumerator = cutoffPercentageNumerator;
		cutoffAmount =
			(int) (getCards().size() * (getCutoffPercentage() + Math.random() * VARIANCE));
		assert cutoffAmount > 0 && cutoffAmount < cards.size()
			: "cutCardIndex <= 0 || cutCardIndex >= cards.size()";

		assert numberOfDecks > 1 : "numberOfDecks <= 1";
		this.numberOfDecks = numberOfDecks;
	}

	@Override
	public Card draw()
	{
		if (getCards().isEmpty())
		{
			throw new NoMoreCardsException(this);
		}

		final var card = cards.poll();

		if (getCards().size() <= getCutoffAmount())
		{
			setShufflingRequired(true);
		}

		return card;
	}

	public List<Card> getCards()
	{
		return List.copyOf(cards);
	}

	public int getCutoffAmount()
	{
		return cutoffAmount;
	}

	public double getCutoffPercentage()
	{
		return cutoffPercentageNumerator / ONE_HUNDRED;
	}

	public double getCutoffPercentageNumerator()
	{
		return cutoffPercentageNumerator;
	}

	public boolean isShufflingRequired()
	{
		return shufflingRequired;
	}

	public int getNumberOfDecks()
	{
		return numberOfDecks;
	}

	private void setShufflingRequired(boolean value)
	{
		shufflingRequired = value;
	}

	@Override
	public String toString()
	{
		return String.format(
			"%s[cards=%s,cutoffAmount=%s,cutoffPercentage=%s,cutoffPercentageNumerator=%s,numberOfDecks=%s,shufflingRequired=%s]",
			getClass().getName(),
			getCards(),
			getCutoffAmount(),
			getCutoffPercentage(),
			getCutoffPercentageNumerator(),
			getNumberOfDecks(),
			isShufflingRequired()
		);
	}
}