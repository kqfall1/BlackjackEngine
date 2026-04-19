package com.github.kqfall1.java.blackjackEngine.model.exceptions;

import com.github.kqfall1.java.blackjackEngine.model.interfaces.Drawable;
import java.io.IOException;

/**
 * Encapsulates data about failures resulting from drawing a {@code Card} from
 * a {@code Drawable} with no available {@code Card} objects.
 *
 * @author kqfall1
 * @since 14/12/2025
 */
public final class NoMoreCardsException extends IOException
{
	public NoMoreCardsException(Drawable cardSource)
	{
		super(String.format("Card source %s is empty.", cardSource));
	}
}