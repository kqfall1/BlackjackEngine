package com.github.kqfall1.java.blackjackEngine.model.exceptions;

import java.io.IOException;

/**
 * Encapsulates data about failures resulting from attempted {@code GameEngine}
 * operations that contradict its encapsulated {@code RuleConfig} object's settings.
 *
 * @author kqfall1
 * @since 15/12/2025
 */
public class RuleViolationException extends IOException
{
	public RuleViolationException(String message)
	{
		super(message);
	}
}