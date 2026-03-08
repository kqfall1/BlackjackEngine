package com.github.kqfall1.java.blackjackEngine.view.swingApplication.jframes;

import com.github.kqfall1.java.blackjackEngine.model.cards.Card;
import com.github.kqfall1.java.blackjackEngine.model.engine.BlackjackEngine;
import com.github.kqfall1.java.blackjackEngine.model.engine.BlackjackRulesetConfiguration;
import com.github.kqfall1.java.blackjackEngine.model.engine.StandardBlackjackRuleset;
import com.github.kqfall1.java.blackjackEngine.model.enums.EngineState;
import com.github.kqfall1.java.blackjackEngine.model.hands.Hand;
import com.github.kqfall1.java.blackjackEngine.model.hands.HandContext;
import com.github.kqfall1.java.blackjackEngine.model.interfaces.BlackjackEngineListener;
import com.github.kqfall1.java.blackjackEngine.view.swingApplication.UiConstants;
import com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents.GameCardsJPanel;
import com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents.GameInfoJPanel;
import com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents.GameActionJPanel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.*;

/**
 * Allows players to use and control a {@code BlackjackEngine} to play a game of blackjack.
 *
 * @author kqfall1
 * @since 21/02/2026
 */
public class GameJFrame extends BlackjackJFrame implements BlackjackEngineListener
{
    private final BlackjackEngine blackjackEngine;
    private final ExecutorService executorService;
    private final GameActionJPanel gameActionJPanel;
    private final JPanel gameCardsJPanel;
    private final GameInfoJPanel gameInfoJPanel;
    private final MainMenuJFrame mainMenuJFrame;

    public GameJFrame(BlackjackRulesetConfiguration config, MainMenuJFrame mainMenuJFrame)
    {
        blackjackEngine = new BlackjackEngine(
            this,
            UiConstants.LOG_FILE_PATH,
            UiConstants.LOGGER_NAME,
            new StandardBlackjackRuleset(config)
        );
        executorService = Executors.newSingleThreadExecutor();
        gameActionJPanel = new GameActionJPanel();
        gameCardsJPanel = new GameCardsJPanel();
        gameInfoJPanel = new GameInfoJPanel(blackjackEngine, executorService);
        this.mainMenuJFrame = mainMenuJFrame;

        add(gameActionJPanel, BorderLayout.EAST);
        add(gameInfoJPanel, BorderLayout.WEST);
        add(gameCardsJPanel, BorderLayout.CENTER);
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
    public void onStateChanged(EngineState oldState)
    {
        switch (blackjackEngine.getState())
        {
            case BETTING ->
            {
                gameInfoJPanel.getAdvanceEngineJButton().setEnabled(false);
                gameInfoJPanel.getPlayerInputJButton().setEnabled(true);
            }
            //will continue and add more states
        }
    }
}