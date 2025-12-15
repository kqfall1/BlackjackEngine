package com.github.kqfall1.java.blackjackEngine.controllers;

/**
 * The 9 main states of a blackjack game.
 *
 * <p>
 * This {@code enum} includes an entries {@code GameState.START} and
 * {@code GameState.END} for the periods of time before and after the game starts
 * and ends, respectively.
 * </p>
 *
 * @author kqfall1
 * @since 14/12/2025
 */
public enum GameState
{
	START,
	BETTING,
	DEALING,
	INSURANCE_CHECK,
	PLAYER_TURN,
	DEALER_TURN,
	SHOWDOWN,
	RESETTING,
	END
}