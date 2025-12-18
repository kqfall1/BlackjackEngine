package com.github.kqfall1.java.blackjackEngine.model.exceptions;

/**
 * Encapsulates data about failures resulting from attempted {@code BlackjackEngine}
 * operations that contradict its encapsulated {@code RuleConfig} object's settings.
 *
 * @author kqfall1
 * @since 15/12/2025
 */
public class RuleViolationException extends Exception
{
	public RuleViolationException(String message)
	{
		super(message);
	}
}