package com.github.kqfall1.java.blackjackEngine.model.enums;

/**
 * Identifies whether a {@code HandContext} object's encapsulated {@code Hand}
 * originated from a normal deal or from a hand split operation in a blackjack
 * betting round.
 *
 * @author kqfall1
 * @since 14/12/2025
 */
public enum HandContextType
{
	MAIN,
	SPLIT
}