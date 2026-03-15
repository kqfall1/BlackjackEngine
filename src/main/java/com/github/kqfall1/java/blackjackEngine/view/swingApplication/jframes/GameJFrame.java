package com.github.kqfall1.java.blackjackEngine.view.swingApplication.jframes;

import com.github.kqfall1.java.blackjackEngine.model.cards.Card;
import com.github.kqfall1.java.blackjackEngine.model.engine.BlackjackEngine;
import com.github.kqfall1.java.blackjackEngine.model.engine.BlackjackRulesetConfiguration;
import com.github.kqfall1.java.blackjackEngine.model.engine.StandardBlackjackRuleset;
import com.github.kqfall1.java.blackjackEngine.model.enums.BlackjackEngineState;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
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

            if (blackjackEngine.getState() == BlackjackEngineState.START)
            {
                executorService.submit(blackjackEngine::start);
            }
            else if (blackjackEngine.getState() == BlackjackEngineState.PLAYER_TURN)
            {
                executorService.submit(() ->
                {
                    blackjackEngine.advanceAfterDrawingRoundCompletedPlayer();

                    if (blackjackEngine.getState() == BlackjackEngineState.DEALER_TURN)
                    {
                        blackjackEngine.dealerTurn();
                        blackjackEngine.advanceAfterDealerTurn();
                    }

                    blackjackEngine.showdown();
                });
            }
            else if (blackjackEngine.getState() == BlackjackEngineState.SHOWDOWN)
            {
                executorService.submit(() ->
                {
                    blackjackEngine.advanceAfterShowdown();
                    blackjackEngine.reset();
                    blackjackEngine.advanceAfterReset();
                });
            }
        });
        gameInfoJPanel.getPlayerInputJButton().addActionListener(e ->
        {
            togglePlayerInputComponents(false);

            if (blackjackEngine.getState() == BlackjackEngineState.BETTING)
            {
                final var FUTURE_INPUT = gameInfoJPanel.getPlayerInputJTextField().getNumber(null, 0, Float.MAX_VALUE);
                FUTURE_INPUT.whenComplete((result, throwable) ->
                {
                    if (throwable == null)
                    {
                        CompletableFuture.completedFuture(result)
                            .thenApplyAsync(BigDecimal::valueOf)
                            .thenAccept(blackjackEngine::placeBet)
                            .whenComplete((betResult, betThrowable) ->
                            {
                                if (betThrowable == null)
                                {
                                    blackjackEngine.deal();
                                    blackjackEngine.advanceAfterDeal();
                                }
                                else
                                {
                                    togglePlayerInputComponents(true);
                                    final var BET_THROWABLE_CAUSE = betThrowable.getCause();

                                    if (BET_THROWABLE_CAUSE instanceof InsufficientChipsException)
                                    {
                                        gameInfoJPanel.presentFailure(betThrowable.getMessage(), gameInfoJPanel.getPlayerChipAmountJLabel());
                                    }
                                    else
                                    {
                                        gameInfoJPanel.presentFailure(
                                            betThrowable.getMessage(),
                                            gameInfoJPanel.getAdvanceEngineJButton(),
                                            gameInfoJPanel.getEngineMessageJTextArea()
                                        );
                                    }
                                }
                            });
                    }
                    else
                    {
                        togglePlayerInputComponents(true);
                        gameInfoJPanel.presentFailure(throwable.getMessage(), gameInfoJPanel.getPlayerInputJTextField());
                    }
                });
            }
            else if (blackjackEngine.getState() == BlackjackEngineState.INSURANCE_CHECK)
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
                        togglePlayerInputComponents(true);
                        gameInfoJPanel.presentFailure(throwable.getMessage(), gameInfoJPanel.getPlayerInputJTextField());
                    }
                });
            }
        });

        add(gameActionJPanel, BorderLayout.EAST);
        add(gameInfoJPanel, BorderLayout.WEST);
        add(gameCardsJPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    @Override
    public void onBetPlaced(HandContext handContext)
    {
        updateUiAfterPlayerChipAmountChanges();
    }

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
    public void onDrawingRoundCompletedPlayer(HandContext handContext)
    {
        SwingUtilities.invokeLater(() ->
        {
            gameActionJPanel.getHitJButton().setEnabled(false);
            gameActionJPanel.getStandJButton().setEnabled(false);
        });
    }

    @Override
    public void onDrawingRoundStartedDealer(Hand dealerHand) {}

    @Override
    public void onDrawingRoundStartedPlayer(HandContext handContext) {}

    @Override
    public void onGameCompleted()
    {
        gameInfoJPanel.getPlayerChipAmountJLabel().setText(UiConstants.GAME_PLAYER_CHIP_AMOUNT_LABEL_PREFIX);
    }

    @Override
    public void onGameStarted()
    {
        updateUiAfterPlayerChipAmountChanges();
    }

    @Override
    public void onInsuranceBetOpportunityDetected(Card dealerUpCard) {}

    @Override
    public void onInsuranceBetResolved(boolean wasSuccessful, BigDecimal playerWinnings)
    {
        if (playerWinnings.equals(BigDecimal.ZERO))
        {
            gameInfoJPanel.getEngineMessageJTextArea().append(String.format("%s\n\n", UiConstants.GAME_INSURANCE_BET_LOST));
        }
        else
        {
            gameInfoJPanel.getEngineMessageJTextArea().append(String.format(
                "%s%.2f.\n\n",
                UiConstants.GAME_INSURANCE_BET_WON_PREFIX,
                playerWinnings
            ));
            updateUiAfterPlayerChipAmountChanges();
        }
    }

    @Override
    public void onPlayerSplit(HandContext currentHand, HandContext splitHand) {}

    @Override
    public void onReset() {}

    @Override
    public void onShowdownCompleted(Hand dealerHand, HandContext handContext, boolean playerWon, BigDecimal playerWinnings) {}

    @Override
    public void onShowdownStarted(Hand dealerHand, HandContext handContext) {}

    @Override
    public void onStateChanged(BlackjackEngineState oldState)
    {
        SwingUtilities.invokeLater(() ->
        {
            switch (blackjackEngine.getState())
            {
                case BETTING, INSURANCE_CHECK ->
                {
                    togglePlayerInputComponents(true);
                }
                case PLAYER_TURN ->
                {
                    gameActionJPanel.getDoubleDownJButton().setEnabled(blackjackEngine.getRuleset().isDoubleDownPossible(
                        blackjackEngine.getActiveHandContext(),
                        blackjackEngine.getState(),
                        blackjackEngine.getPlayer()
                    ));
                    gameActionJPanel.getHitJButton().setEnabled(true);
                    gameActionJPanel.getSplitJButton().setEnabled(blackjackEngine.getRuleset().isSplitPossible(
                        blackjackEngine.getActiveHandContext(),
                        blackjackEngine.getState(),
                        blackjackEngine.getActiveHandContextIndex(),
                        blackjackEngine.getPlayer()
                    ));
                    gameActionJPanel.getStandJButton().setEnabled(true);
                    gameActionJPanel.getSurrenderJButton().setEnabled(blackjackEngine.getRuleset().isSurrenderingPossible(
                        blackjackEngine.getActiveHandContext(),
                        blackjackEngine.getState()
                    ));
                }
            }
        });
    }

    private void togglePlayerInputComponents(boolean enabled)
    {
        gameInfoJPanel.getPlayerInputJButton().setEnabled(enabled);
        gameInfoJPanel.getPlayerInputJTextField().setEnabled(enabled);
    }

    private void updateUiAfterPlayerChipAmountChanges()
    {
        SwingUtilities.invokeLater(() ->
        {
            gameInfoJPanel.getPlayerChipAmountJLabel().setText(String.format(
                "%s%.2f",
                UiConstants.GAME_PLAYER_CHIP_AMOUNT_LABEL_PREFIX,
                blackjackEngine.getPlayer().getChips()
            ));
            gameInfoJPanel.getPlayerInputJTextField().setText("");
        });
    }
}