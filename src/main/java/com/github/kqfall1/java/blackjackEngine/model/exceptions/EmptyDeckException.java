package com.github.kqfall1.java.blackjackEngine.model.exceptions;

import com.github.kqfall1.java.blackjackEngine.model.cards.Deck;
import com.github.kqfall1.java.blackjackEngine.model.entities.Dealer;

/**
 * Encapsulates data about failures resulting from a {@code Dealer} attempting
 * to draw a {@code Card} from an empty {@code Deck}.
 *
 * @author kqfall1
 * @since 14/12/2025
 */
public final class EmptyDeckException extends RuntimeException
{
	public EmptyDeckException(Dealer dealer, Deck deck)
	{
		super(String.format(
			"Dealer %s tried to draw from empty deck %s",
			dealer,
			deck
		));
	}
}