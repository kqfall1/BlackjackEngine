package com.github.kqfall1.java.blackjackEngine.model.cards;

import com.github.kqfall1.java.blackjackEngine.model.enums.Rank;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.NoMoreCardsException;
import com.github.kqfall1.java.blackjackEngine.model.interfaces.Drawable;
import java.util.*;

/**
 * A container for randomly assorted collections of {@code Card} objects.
 *
 * @author kqfall1
 * @since 31/12/2025
 */
public final class Shoe implements Drawable
{
	private final Queue<Card> cards;

	/**
 	 * The amount of {@code Card} objects needed to be left in this {@code Shoe}
	 * object's {@code cards} for {@code shufflingRequired} to be set to {@code true}.
 	 */
	private final int cutoffAmount;

	private final int numberOfDecks;
	public static final double ONE_HUNDRED = 100.0;
	public static final double MAXIMUM_PENETRATION = 90.0;
	public static final double MINIMUM_PENETRATION = 10.0;

	/**
	 * Expresses the smallest percentage of {@code Card} objects in this {@code Shoe}
	 * for {@code shufflingRequired} to remain {@code false}. It is a {@code double}
	 * and must be between {@code 10} and {@code 90} inclusive.
	 */
	private final double penetration;

	/**
 	 * Is used to determine whether the {@code Shoe} should be reused for another
	 * betting round in a blackjack game.
 	 */
	private boolean shufflingRequired;

	/**
 	 * An amount added to the return value of {getPenetration} of this
	 * {@code Shoe} to add variance to the values of different {@code Shoe}
	 * object's {@code cutoffAmount} values.
 	 */
	private static final double VARIANCE = 0.05;

	public Shoe(Rank[] includedRanks, int numberOfDecks, double penetration)
	{
		final List<Card> cardsList = new ArrayList<>();

		for (int count = 0; count < numberOfDecks; count++)
		{
			cardsList.addAll(new Deck(includedRanks).getCards());
		}

		Collections.shuffle(cardsList);
		cards = new ArrayDeque<>(cardsList);

		assert penetration >= MINIMUM_PENETRATION && penetration <= MAXIMUM_PENETRATION
			: "penetration < MINIMUM_PENETRATION || penetration > MAXIMUM_PENETRATION";
		this.penetration = penetration;
		cutoffAmount =
			(int) (getCards().size() * (getPenetrationPercentage() + Math.random() * VARIANCE));
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

	public int getNumberOfDecks()
	{
		return numberOfDecks;
	}

	public double getPenetration()
	{
		return penetration;
	}

	public double getPenetrationPercentage()
	{
		return penetration / ONE_HUNDRED;
	}

	public boolean isShufflingRequired()
	{
		return shufflingRequired;
	}

	private void setShufflingRequired(boolean value)
	{
		shufflingRequired = value;
	}

	@Override
	public String toString()
	{
		return String.format(
			"%s[cards=%s,cutoffAmount=%s,numberOfDecks=%s,penetration=%s,penetrationPercentage=%s,shufflingRequired=%s]",
			getClass().getName(),
			getCards(),
			getCutoffAmount(),
			getNumberOfDecks(),
			getPenetration(),
			getPenetrationPercentage(),
			isShufflingRequired()
		);
	}
}