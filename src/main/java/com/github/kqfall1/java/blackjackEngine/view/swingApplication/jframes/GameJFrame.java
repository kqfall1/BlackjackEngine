package com.github.kqfall1.java.blackjackEngine.view.swingApplication.jframes;

import com.github.kqfall1.java.blackjackEngine.model.cards.Card;
import com.github.kqfall1.java.blackjackEngine.model.engine.BlackjackEngine;
import com.github.kqfall1.java.blackjackEngine.model.engine.BlackjackRulesetConfiguration;
import com.github.kqfall1.java.blackjackEngine.model.engine.StandardBlackjackRuleset;
import com.github.kqfall1.java.blackjackEngine.model.enums.EngineState;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.RuleViolationException;
import com.github.kqfall1.java.blackjackEngine.model.hands.Hand;
import com.github.kqfall1.java.blackjackEngine.model.hands.HandContext;
import com.github.kqfall1.java.blackjackEngine.model.interfaces.BlackjackEngineListener;
import com.github.kqfall1.java.blackjackEngine.view.swingApplication.UiConstants;
import com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents.GameCardsJPanel;
import com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents.GameInfoJPanel;
import com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents.GameActionJPanel;
import com.github.kqfall1.java.enums.YesNoInput;
import java.awt.*;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
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
        gameInfoJPanel = new GameInfoJPanel();
        this.mainMenuJFrame = mainMenuJFrame;

        gameInfoJPanel.getAdvanceEngineJButton().addActionListener(e ->
        {
            gameInfoJPanel.getAdvanceEngineJButton().setEnabled(false);

            if (blackjackEngine.getState() == EngineState.START)
            {
                executorService.submit(blackjackEngine::start);
            }
            else if (blackjackEngine.getState() == EngineState.PLAYER_TURN)
            {

            }
            else
            {
                gameInfoJPanel.getAdvanceEngineJButton().setEnabled(true);
            }
            //will continue and add more states
        });
        gameInfoJPanel.getPlayerInputJButton().addActionListener(e ->
        {
            gameInfoJPanel.getPlayerInputJButton().setEnabled(false);

            if (blackjackEngine.getState() == EngineState.BETTING)
            {
                final var FUTURE_INPUT = gameInfoJPanel.getPlayerInputJTextField().getNumber(null, 0, Double.MAX_VALUE);
                FUTURE_INPUT.whenComplete((result, throwable) ->
                {
                    if (throwable == null)
                    {
                        gameInfoJPanel.getPlayerInputJTextField().setText("");
                        CompletableFuture.completedFuture(result)
                            .thenApplyAsync(BigDecimal::valueOf)
                            .thenAccept(blackjackEngine::placeBet)
                            .thenRun(blackjackEngine::deal)
                            .thenRun(blackjackEngine::advanceAfterDeal);
                    }
                    else
                    {
                        gameInfoJPanel.getPlayerInputJButton().setEnabled(true);

                        if (throwable instanceof InsufficientChipsException)
                        {
                            gameInfoJPanel.presentFailure(throwable.getMessage(), gameInfoJPanel.getPlayerChipAmountJLabel());
                        }
                        else if (throwable instanceof RuleViolationException)
                        {
                            gameInfoJPanel.presentFailure(throwable.getMessage(), gameInfoJPanel.getPlayerInputJButton());
                        }
                        else
                        {
                            gameInfoJPanel.presentFailure(throwable.getMessage(), gameInfoJPanel.getPlayerInputJTextField());
                        }
                    }
                });
            }
            else if (blackjackEngine.getState() == EngineState.INSURANCE_CHECK)
            {
                final var FUTURE_INPUT = gameInfoJPanel.getPlayerInputJTextField().getYesNo(null);
                FUTURE_INPUT.whenComplete((result, throwable) ->
                {
                    if (throwable == null)
                    {
                        if (result == YesNoInput.YES)
                        {
                            CompletableFuture.supplyAsync(blackjackEngine::acceptInsuranceBet, executorService)
                                .thenAccept(blackjackEngine::advanceAfterInsuranceBet);
                        }
                        else
                        {
                            executorService.submit(blackjackEngine::declineInsuranceBet);
                        }
                    }
                    else
                    {
                        gameInfoJPanel.presentFailure(throwable.getMessage(), gameInfoJPanel.getPlayerInputJTextField());
                    }
                });
                gameInfoJPanel.getPlayerInputJButton().setEnabled(false);
                gameInfoJPanel.getPlayerInputJTextField().setText("");
            }
        });

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
        SwingUtilities.invokeLater(() ->
        {
            gameInfoJPanel.getPlayerChipAmountJLabel().setText(String.format(
                "%s%.2f",
                UiConstants.GAME_PLAYER_CHIP_AMOUNT_LABEL_PREFIX,
                blackjackEngine.getPlayer().getChips()
            ));

            switch (blackjackEngine.getState())
            {
                case BETTING, INSURANCE_CHECK ->
                {
                    gameInfoJPanel.getPlayerInputJButton().setEnabled(true);
                }
                case PLAYER_TURN ->
                {
                    gameActionJPanel.getDoubleDownJButton().setEnabled(true);
                    gameActionJPanel.getHitJButton().setEnabled(true);
                    gameActionJPanel.getSplitJButton().setEnabled(true);
                    gameActionJPanel.getStandJButton().setEnabled(true);
                    gameActionJPanel.getSurrenderJButton().setEnabled(true);
                }
                //will continue and add more states
            }
        });
    }
}