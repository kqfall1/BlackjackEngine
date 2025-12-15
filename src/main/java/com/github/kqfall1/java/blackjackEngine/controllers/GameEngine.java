package com.github.kqfall1.java.blackjackEngine.controllers;

import com.github.kqfall1.java.blackjackEngine.model.entities.*;

/**
 * Orchestrates the entire blackjack library by continuously processing input
 * and executing the main game loop.
 *
 * <p>
 * Centralizes all logic that influences gameplay and also emits all internal
 * events for GUI handling.
 * </p>
 */
public class GameEngine
{
	private final RuleConfig config;
	private final Dealer dealer;
	private final Player player;
	private GameState state;

	public GameEngine(RuleConfig config)
	{
		assert config != null : "config == null";
		this.config = config;
		this.dealer = new Dealer();
		this.player = new Player();
		setState(GameState.START);
	}

	public RuleConfig getConfig()
	{
		return config;
	}

	public Dealer getDealer()
	{
		return dealer;
	}

	public Player getPlayer()
	{
		return player;
	}

	public GameState getState()
	{
		return state;
	}

	private void setState(GameState state)
	{
		assert state != null : "state == null";
		this.state = state;
	}

	@Override
	public String toString()
	{
		return String.format(
			"%s[config=%s,dealer=%s,player=%s,state=%s]",
			getClass().getName(),
			getConfig(),
			getDealer(),
			getPlayer(),
			getState()
		);
	}
}