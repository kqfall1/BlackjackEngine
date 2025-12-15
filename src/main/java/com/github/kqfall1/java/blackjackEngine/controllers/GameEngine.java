package com.github.kqfall1.java.blackjackEngine.controllers;

import com.github.kqfall1.java.blackjackEngine.model.betting.Bet;
import com.github.kqfall1.java.blackjackEngine.model.betting.Pot;
import com.github.kqfall1.java.blackjackEngine.model.cards.Card;
import com.github.kqfall1.java.blackjackEngine.model.engine.EngineState;
import com.github.kqfall1.java.blackjackEngine.model.engine.RuleConfig;
import com.github.kqfall1.java.blackjackEngine.model.entities.*;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import com.github.kqfall1.java.blackjackEngine.model.hands.Hand;
import com.github.kqfall1.java.blackjackEngine.model.hands.HandType;
import com.github.kqfall1.java.blackjackEngine.model.hands.PlayerHand;
import com.github.kqfall1.java.blackjackEngine.model.interfaces.EngineListener;
import com.github.kqfall1.java.utils.LoggerUtils;
import com.github.kqfall1.java.utils.StringUtils;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.logging.Logger;

/**
 * Orchestrates the entire blackjack library by continuously processing input and
 * executing the main game loop.
 *
 * <p>
 * Centralizes all logic that influences gameplay as a public API, emits all internal
 * events through {@code EngineListener} hooks, and logs pertinent information.
 * </p>
 */
public class GameEngine
{
	private final RuleConfig config;
	private final Dealer dealer;
	private Bet insuranceBet;
	private final Pot insurancePot;
	private final EngineListener listener;
	private final Logger logger;
	private final Pot mainPot;
	private final Player player;
	private EngineState state;

	public GameEngine(RuleConfig config, EngineListener listener,
					  String loggerFilePath, String loggerName) throws IOException
	{
		assert config != null : "config == null";
		assert listener != null : "listener == null";
		assert loggerFilePath != null : "loggerFilePath == null";
		assert loggerName != null : "loggerName == null";
		this.config = config;
		dealer = new Dealer();
		insurancePot = new Pot();
		this.listener = listener;
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
		final var playerHands = getPlayer().getHands();
		assert dealerHand.getCards().isEmpty() : "!dealerHand.getCards().isEmpty()";
		assert playerHands.getFirst().getHand().getCards().isEmpty()
			: "!playerHands.getFirst().getHand().getCards().isEmpty()";

		for (int count = 0; count < RuleConfig.INITIAL_CARD_COUNT; count++)
		{
			dealCardForPlayer(playerHands.getFirst());
			dealCardForDealer(dealerHand);
		}
		assert playerHands.getFirst().getHand().getCards().size() == RuleConfig.INITIAL_CARD_COUNT
			: "playerHands.getFirst().getHand().getCards().size() != RuleConfig.INITIAL_CARD_COUNT";
		assert dealerHand.getCards().size() == RuleConfig.INITIAL_CARD_COUNT
			: "dealerHand.getCards().size() != RuleConfig.INITIAL_CARD_COUNT";

		logger.info(String.format(
			"The cards have been dealt. Player's hand: %s. The dealer's up card is %s.",
			playerHands,
			dealerHand.getCards().get(RuleConfig.INITIAL_CARD_COUNT - 1)
		));
		logger.exiting("GameEngine", "deal");
		//if dealer's upcard is ace then call insuranceCheck
	}

	private void dealCardForDealer(Hand dealerHand)
	{
		logger.entering("GameEngine", "dealCardForDealer",
			dealerHand);
		assert getState() == EngineState.DEALING : "getState() != EngineState.DEALING";
		final var card = getDealer().hit();
		dealerHand.addCard(card);
		onCardDealtToDealer(card, dealerHand);
		logger.exiting("GameEngine", "dealCardForDealer", card);
	}

	private void dealCardForPlayer(PlayerHand playerHand)
	{
		logger.entering("GameEngine", "dealCardForPlayer",
			playerHand);
		assert getState() == EngineState.DEALING : "getState() != EngineState.DEALING";
		final var card = getDealer().hit();
		playerHand.getHand().addCard(card);
		onCardDealtToPlayer(card, playerHand);
		logger.exiting("GameEngine", "dealCardForPlayer", card);
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

	public EngineListener getListener()
	{
		return listener;
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

	public void insuranceCheck()
	{
		logger.entering("GameEngine", "insuranceCheck");
		assert getState() == EngineState.DEALING : "getState() != EngineState.DEALING";
		setState(EngineState.INSURANCE_CHECK);
		final var dealerHand = getDealer().getHand();
		final var playerHand = getPlayer().getHands().getFirst().getHand();

		if (dealerHand.isBlackjack())
		{

		}
		else
		{

		}
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
		getListener().onBetPlaced(getPlayer(), playerHand);
		logger.info(String.format(
			"Player %s has placed a bet of $%.2f on their %s hand.",
			getPlayer(),
			playerHand.getBet().getAmount(),
			StringUtils.normalizeLower(playerHand.getType().toString())
		));
		logger.exiting("GameEngine", "onBetPlaced");
	}

	private void onCardDealtToDealer(Card card, Hand dealerHand)
	{
		logger.entering("GameEngine", "onCardDealtToDealer",
			new Object[] {card, dealerHand});
		assert card != null : "card == null";
		assert dealerHand != null : "hand == null";
		assert getState() == EngineState.DEALING : "getState() != EngineState.DEALING";
		getListener().onCardDealtToDealer(card, dealerHand);
		logger.info(String.format(
			"Added card %s to dealer's hand %s.",
			card, dealerHand
		));
		logger.exiting("GameEngine", "onCardDealtToDealer");
	}

	private void onCardDealtToPlayer(Card card, PlayerHand playerHand)
	{
		logger.entering("GameEngine", "onCardDealtToPlayer",
			new Object[] {card, playerHand});
		assert card != null : "card == null";
		assert playerHand != null : "playerHand == null";
		assert getState() == EngineState.DEALING : "getState() != EngineState.DEALING";
		getListener().onCardDealtToPlayer(card, playerHand);
		logger.info(String.format(
			"Added card %s to player's hand %s.",
			card, playerHand
		));
		logger.exiting("GameEngine", "onCardDealtToDealer");
	}

	private void setInsuranceBet(Bet insuranceBet)
	{
		assert insuranceBet != null : "insuranceBet == null";
		this.insuranceBet = insuranceBet;
	}

	private void setState(EngineState state)
	{
		logger.entering("GameEngine", "setState", state);
		assert state != null : "state == null";
		final var oldState = getState();
		this.state = state;
		getListener().onStateChanged(oldState, state);
		logger.exiting("GameEngine", "setState");
	}

	@Override
	public String toString()
	{
		return String.format(
			"%s[config=%s,dealer=%s,insuranceBet=%s,insurancePot=%s,listener=%s,logger=%s,mainPot=%s,player=%s,state=%s]",
			getClass().getName(),
			getConfig(),
			getDealer(),
			getInsuranceBet() != null ? getInsuranceBet() : "null",
			getInsurancePot(),
			getListener(),
			getLogger(),
			getMainPot(),
			getPlayer(),
			getState()
		);
	}
}