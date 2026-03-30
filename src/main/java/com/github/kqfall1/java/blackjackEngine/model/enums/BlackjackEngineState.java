package com.github.kqfall1.java.blackjackEngine.model.enums;

/**
 * The 10 states of a blackjack game.
 *
 * <p>
 * This {@code enum} includes an entries {@code BlackjackEngineState.START} and
 * {@code BlackjackEngineState.END} for the periods of time before and after the game starts
 * and ends, respectively.
 * </p>
 *
 * @author kqfall1
 * @since 14/12/2025
 */
public enum BlackjackEngineState
{
	START,
	BETTING,
	DEALING,
	INSURANCE_CHECK,
	PLAYER_TURN,
	DEALER_TURN,
	SHOWING_DOWN,
	SHOWING_DOWN_FINAL_HAND,
	RESETTING,
	END
}