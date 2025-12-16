package com.github.kqfall1.java.blackjackEngine.model.exceptions;

import com.github.kqfall1.java.blackjackEngine.model.hands.Hand;
import java.io.IOException;

/**
 * Encapsulates data about failures resulting from illegal operations on
 * {@code PlayerHand} objects.
 *
 * <p>
 * The most prominent use case of this abstraction is preventing a {@code Player} from
 * doubling down, splitting, or surrendering when the {@code Player} has more than two
 * {@code Card} objects encapsulated within its {@code hand}. Another example is preventing
 * a {@code Player} from splitting if they don't have a pocket pair.
 * </p>
 *
 * @author kqfall1
 * @since 15/12/2025
 */
public class IllegalHandOperationException extends IOException
{
	public IllegalHandOperationException(Hand hand, String message)
	{
		super(String.format(
			"An illegal operation was attempted on hand %s. %s",
			hand,
			message
		));
	}
}