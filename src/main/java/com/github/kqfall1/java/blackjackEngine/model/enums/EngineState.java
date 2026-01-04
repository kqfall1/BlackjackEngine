package com.github.kqfall1.java.blackjackEngine.model.enums;

/**
 * The 9 states of a blackjack game.
 *
 * <p>
 * This {@code enum} includes an entries {@code EngineState.START} and
 * {@code EngineState.END} for the periods of time before and after the game starts
 * and ends, respectively.
 * </p>
 *
 * @author kqfall1
 * @since 14/12/2025
 */
public enum EngineState
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