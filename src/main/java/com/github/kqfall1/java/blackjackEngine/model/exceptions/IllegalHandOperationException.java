package com.github.kqfall1.java.blackjackEngine.model.exceptions;

import com.github.kqfall1.java.blackjackEngine.model.hands.HandContext;

/**
 * Encapsulates data about failures resulting from illegal operations on {@code HandContext} objects.
 *
 * @author kqfall1
 * @since 15/12/2025
 */
public final class IllegalHandOperationException extends RuntimeException
{
	public IllegalHandOperationException(HandContext handContext, String message)
	{
		super(String.format("An illegal operation was attempted on hand context %s. %s", handContext, message));
	}
}