package com.github.kqfall1.java.blackjackEngine.model.exceptions;

import com.github.kqfall1.java.blackjackEngine.model.entities.Player;
import java.math.BigDecimal;

/**
 * Encapsulates data about failures resulting from a {@code Player} attempting
 * to perform an action that would bring their {@code chips} amount below 0.
 *
 * @author kqfall1
 * @since 14/12/2025
 */
public final class InsufficientChipsException extends RuntimeException
{
	public InsufficientChipsException(Player player, BigDecimal requiredChips)
	{
		super(String.format(
			"Player %s has insufficient chips to bet $%s.",
			player,
			requiredChips.toPlainString()
		));
	}
}