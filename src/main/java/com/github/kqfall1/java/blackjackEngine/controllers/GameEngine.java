package com.github.kqfall1.java.blackjackEngine.controllers;

import com.github.kqfall1.java.blackjackEngine.model.betting.Bet;
import com.github.kqfall1.java.blackjackEngine.model.betting.Pot;
import com.github.kqfall1.java.blackjackEngine.model.entities.*;
import com.github.kqfall1.java.utils.LoggerUtils;
import java.io.IOException;
import java.util.logging.Logger;

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
	private Bet insuranceBet;
	private final Pot insurancePot;
	private final Logger logger;
	private final Pot mainPot;
	private final Player player;
	private GameState state;

	public GameEngine(RuleConfig config, String loggerFilePath,
					  String loggerName) throws IOException
	{
		assert config != null : "config == null";
		this.config = config;
		dealer = new Dealer();
		insurancePot = new Pot();
		assert loggerFilePath != null : "loggerFilePath == null";
		assert loggerName != null : "loggerName == null";
		logger = LoggerUtils.newFileLogger(loggerFilePath, loggerName,
			true);
		mainPot = new Pot();
		player = new Player();
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

	public Bet getInsuranceBet()
	{
		return insuranceBet;
	}

	public Pot getInsurancePot()
	{
		return insurancePot;
	}

	public Logger getLogger()
	{
		return logger;
	}

	public Pot getMainPot()
	{
		return mainPot;
	}

	public Player getPlayer()
	{
		return player;
	}

	public GameState getState()
	{
		return state;
	}

	private void setInsuranceBet(Bet insuranceBet)
	{
		assert insuranceBet != null : "insuranceBet == null";
		this.insuranceBet = insuranceBet;
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
			"%s[config=%s,dealer=%s,insuranceBet=%s,insurancePot=%s,logger=%s,mainPot=%s,player=%s,state=%s]",
			getClass().getName(),
			getConfig(),
			getDealer(),
			getInsuranceBet() != null ? getInsuranceBet() : "null",
			getInsurancePot(),
			getLogger(),
			getMainPot(),
			getPlayer(),
			getState()
		);
	}
}