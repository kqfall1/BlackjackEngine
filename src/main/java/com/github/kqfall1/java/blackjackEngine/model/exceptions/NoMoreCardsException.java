package com.github.kqfall1.java.blackjackEngine.model.exceptions;

import com.github.kqfall1.java.blackjackEngine.model.interfaces.Drawable;

/**
 * Encapsulates data about failures resulting from drawing a {@code Card} from
 * a {@code Deck} or {@code Shoe} with no available {@code Card} objects.
 *
 * @author kqfall1
 * @since 14/12/2025
 */
public final class NoMoreCardsException extends RuntimeException
{
	public NoMoreCardsException(Drawable cardSource)
	{
		super(String.format("Card source %s is empty.", cardSource));
	}
}