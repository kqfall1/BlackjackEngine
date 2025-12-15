package com.github.kqfall1.java.blackjackEngine.controllers;

import com.github.kqfall1.java.blackjackEngine.model.betting.Bet;
import com.github.kqfall1.java.blackjackEngine.model.betting.Pot;
import com.github.kqfall1.java.blackjackEngine.model.cards.Card;
import com.github.kqfall1.java.blackjackEngine.model.entities.*;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import com.github.kqfall1.java.blackjackEngine.model.hands.Hand;
import com.github.kqfall1.java.blackjackEngine.model.hands.HandType;
import com.github.kqfall1.java.blackjackEngine.model.hands.PlayerHand;
import com.github.kqfall1.java.utils.LoggerUtils;
import com.github.kqfall1.java.utils.StringUtils;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
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
	private EngineState state;

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
		setState(EngineState.START);
	}

	public void deal()
	{
		logger.entering("GameEngine", "deal");
		assert getState() == EngineState.BETTING
			: "getState() != EngineState.BETTING";

		setState(EngineState.DEALING);
		final var dealerHand = getDealer().getHand();
		final var playerHand = getPlayer().getHands().getFirst().getHand();

		assert dealerHand.getCards().isEmpty() : "!dealerHand.getCards().isEmpty()";
		assert playerHand.getCards().isEmpty() : "!playerHand.getCards().isEmpty()";

		for (int count = 0; count < RuleConfig.INITIAL_CARD_COUNT; count++)
		{
			drawCard(playerHand);
			drawCard(dealerHand);
		}

		assert playerHand.getCards().size() == RuleConfig.INITIAL_CARD_COUNT
			: "playerHand.getCards().size() != RuleConfig.INITIAL_CARD_COUNT";
		assert dealerHand.getCards().size() == RuleConfig.INITIAL_CARD_COUNT
			: "dealerHand.getCards().size() != RuleConfig.INITIAL_CARD_COUNT";

		logger.info(String.format(
			"The cards have been dealt. Player's hand: %s. The dealer's up card is %s.",
			playerHand,
			dealerHand.getCards().get(RuleConfig.INITIAL_CARD_COUNT - 1))
		);
		logger.exiting("GameEngine", "deal");
	}

	private void drawCard(Hand hand)
	{
		logger.entering("GameEngine",  "drawCard", hand);
		assert getState() == EngineState.DEALING : "getState() != EngineState.DEALING";
		final var card = getDealer().hit();
		hand.addCard(card);
		onCardAdded(card, hand);
		logger.exiting("GameEngine", "drawCard", card);
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

	public EngineState getState()
	{
		return state;
	}

	public void placeMainBet(BigDecimal amount)
	{
		logger.entering("GameEngine", "placeBet", amount);
		assert amount != null && amount.compareTo(BigDecimal.ZERO) > 0
			: "amount == null || amount.compareTo(BigDecimal.ZERO) <= 0";
		assert getState() == EngineState.BETTING
				|| getState() == EngineState.RESETTING
				|| getState() == EngineState.START
			: "getState() != EngineState.BETTING && getState() != EngineState.RESETTING && getState() != EngineState.START";

		setState(EngineState.BETTING);

		try
		{
			getPlayer().setChips(getPlayer().getChips().subtract(amount));
		}
		catch (InsufficientChipsException e)
		{
			logger.throwing("GameEngine", "placeMainBet", e);
			throw e;
		}

		final var playerMainHand = new PlayerHand(
			new Bet(amount),
			HandType.MAIN
		);
		getPlayer().addHand(playerMainHand);
		getMainPot().addChips(amount);
		onBetPlaced(playerMainHand);
		logger.exiting("GameEngine", "placeBet", playerMainHand);
		deal();
	}

	private void onBetPlaced(PlayerHand playerHand)
	{
		logger.entering("GameEngine", "onBetPlaced", playerHand);
		assert playerHand != null : "playerHand == null";
		assert getState() == EngineState.BETTING : "getState() != EngineState.BETTING";

		logger.info(String.format(
			"Player %s has placed a bet of $%.2f on their %s hand.",
			getPlayer(),
			playerHand.getBet().getAmount(),
			StringUtils.normalizeLower(playerHand.getType().toString())
		));

		//INTERACT WITH APP CONTROLLER FOR GUI COORDINATION

		logger.exiting("GameEngine", "onBetPlaced");
	}

	private void onCardAdded(Card card, Hand hand)
	{
		logger.entering("GameEngine", "onCardAdded",
			new Object[] {card, hand});
		assert card != null : "card == null";
		assert hand != null : "hand == null";
		assert getState() == EngineState.DEALING : "getState() != EngineState.DEALING";

		logger.info(String.format(
			"Added card %s to hand %s.",
			card,
			hand
		));

		//INTERACT WITH APP CONTROLLER FOR GUI COORDINATION'
		logger.exiting("GameEngine", "onCardAdded");
	}

	private void setInsuranceBet(Bet insuranceBet)
	{
		assert insuranceBet != null : "insuranceBet == null";
		this.insuranceBet = insuranceBet;
	}

	private void setState(EngineState state)
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