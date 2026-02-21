package com.github.kqfall1.java.blackjackEngine.view.swingApplication.jframes;

import com.github.kqfall1.java.blackjackEngine.model.cards.Card;
import com.github.kqfall1.java.blackjackEngine.model.engine.BlackjackEngine;
import com.github.kqfall1.java.blackjackEngine.model.engine.BlackjackRulesetConfiguration;
import com.github.kqfall1.java.blackjackEngine.model.enums.EngineState;
import com.github.kqfall1.java.blackjackEngine.model.hands.Hand;
import com.github.kqfall1.java.blackjackEngine.model.hands.HandContext;
import com.github.kqfall1.java.blackjackEngine.model.interfaces.BlackjackEngineListener;
import java.math.BigDecimal;

/**
 * Allows players to use and control a {@code BlackjackEngine} to play a game of blackjack.
 *
 * @author kqfall1
 * @since 21/02/2026
 */
public class GameJFrame extends BlackjackJFrame implements BlackjackEngineListener
{
    private BlackjackEngine blackjackEngine;
    private BlackjackRulesetConfiguration config;
    private MainMenuJFrame mainMenuJFrame;

    public GameJFrame(BlackjackRulesetConfiguration config, MainMenuJFrame mainMenuJFrame)
    {
        this.config = config;
        this.mainMenuJFrame = mainMenuJFrame;
        setVisible(true);
    }

    @Override
    public void onBetPlaced(HandContext handContext) {}

    @Override
    public void onBettingRoundCompleted() {}

    @Override
    public void onBettingRoundStarted() {}

    @Override
    public void onCardDealtToDealer(Card card, Hand dealerHand, boolean isFaceUp) {}

    @Override
    public void onCardDealtToPlayer(Card card, HandContext handContext) {}

    @Override
    public void onDrawingRoundCompletedDealer(Hand dealerHand) {}

    @Override
    public void onDrawingRoundCompletedPlayer(HandContext handContext) {}

    @Override
    public void onDrawingRoundStartedDealer(Hand dealerHand) {}

    @Override
    public void onDrawingRoundStartedPlayer(HandContext handContext) {}

    @Override
    public void onGameCompleted() {}

    @Override
    public void onGameStarted() {}

    @Override
    public void onInsuranceBetOpportunityDetected(Card dealerUpCard) {}

    @Override
    public void onInsuranceBetResolved(boolean wasSuccessful, BigDecimal playerWinnings) {}

    @Override
    public void onPlayerSplit(HandContext currentHand, HandContext splitHand) {}

    @Override
    public void onReset() {}

    @Override
    public void onShowdownCompleted(Hand dealerHand, HandContext handContext, boolean playerWon, BigDecimal playerWinnings) {}

    @Override
    public void onShowdownStarted(Hand dealerHand, HandContext handContext) {}

    @Override
    public void onStateChanged(EngineState oldState) {}

    public void setBlackjackEngine(BlackjackEngine blackjackEngine)
    {
        this.blackjackEngine = blackjackEngine;
    }
}