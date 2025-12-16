package com.github.kqfall1.java.blackjackEngine.model.exceptions;

import com.github.kqfall1.java.blackjackEngine.model.cards.Deck;

/**
 * Encapsulates data about failures resulting from a {@code Dealer} drawing a
 * {@code Card} from an empty {@code Deck}.
 *
 * @author kqfall1
 * @since 14/12/2025
 */
public class EmptyDeckException extends RuntimeException
{
	public EmptyDeckException(Deck deck)
	{
		super(String.format("Deck %s is empty.", deck));
	}
}
