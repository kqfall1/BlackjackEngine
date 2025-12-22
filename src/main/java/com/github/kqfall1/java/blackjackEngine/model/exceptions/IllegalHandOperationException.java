package com.github.kqfall1.java.blackjackEngine.model.exceptions;

import com.github.kqfall1.java.blackjackEngine.model.hands.HandContext;

/**
 * Encapsulates data about failures resulting from illegal operations on
 * {@code HandContext} objects.
 *
 * <p>
 * The most prominent use case of this abstraction is preventing a {@code Player} from
 * doubling down, splitting, or surrendering when the {@code Player} has more than two
 * {@code Card} objects encapsulated within its active {@code HandContext}. Another
 * example is preventing a {@code HandContext} from splitting if its {@code Hand} is not
 * a pocket pair.
 * </p>
 *
 * @author kqfall1
 * @since 15/12/2025
 */
public final class IllegalHandOperationException extends Exception
{
	public IllegalHandOperationException(HandContext handContext, String message)
	{
		super(String.format(
			"An illegal operation was attempted on hand context %s. %s",
			handContext,
			message
		));
	}
}