package com.github.kqfall1.java.blackjackEngine.view.swingApplication.jframes;

import com.github.kqfall1.java.blackjackEngine.model.cards.Card;
import com.github.kqfall1.java.blackjackEngine.model.engine.BlackjackConstants;
import com.github.kqfall1.java.blackjackEngine.model.engine.BlackjackEngine;
import com.github.kqfall1.java.blackjackEngine.model.engine.BlackjackRulesetConfiguration;
import com.github.kqfall1.java.blackjackEngine.model.engine.StandardBlackjackRuleset;
import com.github.kqfall1.java.blackjackEngine.model.enums.BlackjackEngineState;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import com.github.kqfall1.java.blackjackEngine.model.hands.Hand;
import com.github.kqfall1.java.blackjackEngine.model.hands.HandContext;
import com.github.kqfall1.java.blackjackEngine.model.interfaces.BlackjackEngineListener;
import com.github.kqfall1.java.blackjackEngine.view.swingApplication.UiActions;
import com.github.kqfall1.java.blackjackEngine.view.swingApplication.UiConstants;
import com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents.CardJLabel;
import com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents.GameCardsJPanel;
import com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents.GameInfoJPanel;
import com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents.GameActionJPanel;
import com.github.kqfall1.java.enums.YesNoInput;
import com.github.kqfall1.java.javax.swing.AwtUtils;
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
    private final GameCardsJPanel gameCardsJPanel;
    private final GameInfoJPanel gameInfoJPanel;
    private final MainMenuJFrame mainMenuJFrame;

    public GameJFrame(BlackjackRulesetConfiguration config, MainMenuJFrame mainMenuJFrame)
    {
        final var ACTION_MAP = getRootPane().getActionMap();
        final var INPUT_MAP = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        blackjackEngine = new BlackjackEngine(
                this,
                UiConstants.BLACKJACK_ENGINE_LOG_FILE_PATH,
                UiConstants.BLACKJACK_ENGINE_LOGGER_NAME,
                new StandardBlackjackRuleset(config)
        );
        executorService = Executors.newSingleThreadExecutor();

        final var DOUBLE_DOWN = UiActions.getInstance().getGameAction(e ->
        {
            executorService.submit(blackjackEngine::playerDoubleDown);
            updateUiAfterPlayerChipAmountChanges();
        }, UiConstants.GAME_ACTION_DOUBLE_DOWN_LABEL, ACTION_MAP, INPUT_MAP, KeyStroke.getKeyStroke("ctrl D"));
        final var HIT = UiActions.getInstance().getGameAction(
            e -> executorService.submit(blackjackEngine::playerHit),
            UiConstants.GAME_ACTION_HIT_LABEL,
            ACTION_MAP,
            INPUT_MAP,
            KeyStroke.getKeyStroke("ctrl H")
        );
        final var SPLIT = UiActions.getInstance().getGameAction(e ->
        {
            executorService.submit(blackjackEngine::playerSplit);
            updateUiAfterPlayerChipAmountChanges();
        }, UiConstants.GAME_ACTION_SPLIT_LABEL, ACTION_MAP, INPUT_MAP, KeyStroke.getKeyStroke("ctrl P"));
        final var STAND = UiActions.getInstance().getGameAction(
            e -> executorService.submit(blackjackEngine::playerStand),
            UiConstants.GAME_ACTION_STAND_LABEL,
            ACTION_MAP,
            INPUT_MAP,
            KeyStroke.getKeyStroke("ctrl T")
        );
        final var SURRENDER = UiActions.getInstance().getGameAction(
            e -> executorService.submit(blackjackEngine::playerSurrender),
            UiConstants.GAME_ACTION_SURRENDER_LABEL,
            ACTION_MAP,
            INPUT_MAP,
            KeyStroke.getKeyStroke("ctrl X")
        );

        gameActionJPanel = new GameActionJPanel(DOUBLE_DOWN, HIT, SPLIT, STAND, SURRENDER);
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
                executorService.submit(blackjackEngine::advanceAfterDrawingRoundCompletedPlayer);
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
                        gameInfoJPanel.presentFailure(
                            UiConstants.getInputErrorMessage(gameInfoJPanel.getPlayerInputJTextField().getText().trim()),
                            gameInfoJPanel.getPlayerInputJTextField()
                        );
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

    private void clearActivePlayerHandJPanel()
    {
        SwingUtilities.invokeLater(() ->
        {
            gameCardsJPanel.getActivePlayerHandJPanel().removeAll();
            gameCardsJPanel.revalidate();
            gameCardsJPanel.repaint();
        });
    }

    @Override
    public void onBetPlaced(HandContext handContext)
    {
        gameInfoJPanel.getEngineMessageJTextArea().append(String.format(
            "%s%.2f.\n\n", UiConstants.GAME_MESSAGE_BET_PLACED, handContext.getBet().getAmount().doubleValue()
        ));
        updateUiAfterPlayerChipAmountChanges();
    }

    @Override
    public void onBettingRoundCompleted() {}

    @Override
    public void onBettingRoundStarted() {}

    @Override
    public void onCardDealtToDealer(Card card, Hand dealerHand, boolean isFaceUp)
    {
        SwingUtilities.invokeLater(() ->
        {
            gameCardsJPanel.getDealerHandJPanel().add(new CardJLabel(card, isFaceUp));
            gameCardsJPanel.getDealerHandJPanel().revalidate();
            gameCardsJPanel.getDealerHandJPanel().repaint();
        });
    }

    @Override
    public void onCardDealtToPlayer(Card card, HandContext handContext)
    {
        renderCardForPlayer(card);
        updateUiForPlayerScore();
    }

    @Override
    public void onDrawingRoundCompletedDealer(Hand dealerHand) {}

    @Override
    public void onDrawingRoundCompletedPlayer(HandContext handContext)
    {
        final boolean ALL_HAND_CONTEXTS_DRAWN = blackjackEngine.isPlayerActingOnFinalHandContext();
        SwingUtilities.invokeLater(() ->
        {
            for (Component component : AwtUtils.getNestedComponents(gameActionJPanel))
            {
                if (component instanceof JButton jbutton)
                {
                    jbutton.getAction().setEnabled(false);
                }
            }

            gameInfoJPanel.getAdvanceEngineJButton().setEnabled(!ALL_HAND_CONTEXTS_DRAWN);
        });

        if (ALL_HAND_CONTEXTS_DRAWN)
        {
            blackjackEngine.advanceAfterDrawingRoundCompletedPlayer();
        }
    }

    @Override
    public void onDrawingRoundStartedDealer(Hand dealerHand) {}

    @Override
    public void onDrawingRoundStartedPlayer(HandContext handContext)
    {
        clearActivePlayerHandJPanel();

        for (Card card : handContext.getHand().getCards())
        {
            renderCardForPlayer(card);
        }

        updateUiForPlayerScore();
    }

    @Override
    public void onGameCompleted()
    {
        SwingUtilities.invokeLater(() ->
        {
            gameInfoJPanel.getPlayerChipAmountJLabel().setText(UiConstants.GAME_INFO_JPANEL_PLAYER_CHIP_AMOUNT_LABEL);
            gameInfoJPanel.getEngineMessageJTextArea().append(String.format("%s\n\n", UiConstants.GAME_MESSAGE_BUSTED));
            updateUiForEngineMessage();
        });
    }

    @Override
    public void onGameStarted()
    {
        updateUiAfterPlayerChipAmountChanges();
    }

    @Override
    public void onInsuranceBetOpportunityDetected(Card dealerUpCard)
    {
        gameInfoJPanel.getEngineMessageJTextArea().append(String.format(
            "%s\n\n", UiConstants.GAME_MESSAGE_INSURANCE_BET_NOTIFICATION
        ));
        updateUiForEngineMessage();
    }

    @Override
    public void onInsuranceBetResolved(boolean wasSuccessful, BigDecimal playerWinnings)
    {
        SwingUtilities.invokeLater(() ->
        {
            if (wasSuccessful)
            {
                gameInfoJPanel.getEngineMessageJTextArea().append(String.format(
                    "%s%.2f.\n\n",
                    UiConstants.GAME_MESSAGE_INSURANCE_BET_WON,
                    playerWinnings.doubleValue()
                ));
            }
            else
            {
                gameInfoJPanel.getEngineMessageJTextArea().append(String.format("%s\n\n", UiConstants.GAME_MESSAGE_INSURANCE_BET_LOST));
                updateUiForEngineMessage();
            }
        });
        updateUiAfterPlayerChipAmountChanges();
    }

    @Override
    public void onPlayerSplit(HandContext currentHand, HandContext splitHand)
    {
        gameInfoJPanel.getEngineMessageJTextArea().append(String.format("%s\n\n", UiConstants.GAME_MESSAGE_SPLIT));
    }

    @Override
    public void onReset()
    {
        clearActivePlayerHandJPanel();
        SwingUtilities.invokeLater(() ->
        {
            gameCardsJPanel.getDealerHandJPanel().removeAll();
            gameCardsJPanel.revalidate();
            gameCardsJPanel.repaint();

            gameInfoJPanel.getDealerHandScoreJLabel().setText(UiConstants.GAME_INFO_JPANEL_DEALER_HAND_SCORE_LABEL);
            gameInfoJPanel.getActiveHandContextHandScoreJLabel().setText(UiConstants.GAME_INFO_JPANEL_ACTIVE_HAND_CONTEXT_HAND_SCORE_LABEL);
        });
    }

    @Override
    public void onShowdownCompleted(Hand dealerHand, HandContext handContext, boolean playerWon, BigDecimal playerWinnings)
    {
        if (playerWon)
        {
            SwingUtilities.invokeLater(() ->
            {
                gameCardsJPanel.getActivePlayerHandJPanel().setBackground(Color.GREEN);
                gameCardsJPanel.getActivePlayerHandJPanel().setOpaque(true);
                gameInfoJPanel.getEngineMessageJTextArea().append(String.format("%s\n\n", UiConstants.GAME_MESSAGE_SHOWDOWN_WON));

                new Timer(UiConstants.SLEEP_INTERVAL, e ->
                {
                    gameCardsJPanel.getActivePlayerHandJPanel().setBackground(null);
                    gameCardsJPanel.getActivePlayerHandJPanel().setOpaque(false);
                    ((Timer) e.getSource()).stop();
                }).start();
                updateUiForEngineMessage();
            });
        }

        SwingUtilities.invokeLater(() ->
        {
            gameInfoJPanel.getAdvanceEngineJButton().setEnabled(true);
            gameInfoJPanel.getEngineMessageJTextArea().append(String.format(
                "%s%.2f.\n\n", UiConstants.GAME_MESSAGE_SHOWDOWN_COLLECTION, playerWinnings
            ));
            gameInfoJPanel.getDealerHandScoreJLabel().setText(String.format(
                "%s%d", UiConstants.GAME_INFO_JPANEL_DEALER_HAND_SCORE_LABEL, dealerHand.getScore()
            ));
        });
        updateUiAfterPlayerChipAmountChanges();
    }

    @Override
    public void onShowdownStarted(Hand dealerHand, HandContext handContext)
    {
        SwingUtilities.invokeLater(() ->
        {
            gameInfoJPanel.getAdvanceEngineJButton().setEnabled(false);
            ((CardJLabel) gameCardsJPanel.getDealerHandJPanel().getComponents()[BlackjackConstants.INITIAL_CARD_COUNT - 1]).setFaceUp(true);
        });
        //implement card-rendering logic
    }

    @Override
    public void onStateChanged(BlackjackEngineState oldState)
    {
        switch (blackjackEngine.getState())
        {
            case BETTING, INSURANCE_CHECK -> togglePlayerInputComponents(true);
            case PLAYER_TURN -> SwingUtilities.invokeLater(() ->
            {
                gameActionJPanel.getDoubleDownJButton().getAction().setEnabled(blackjackEngine.getRuleset().isDoublingDownPossible(
                    blackjackEngine.getActiveHandContext(),
                    blackjackEngine.getState(),
                    blackjackEngine.getPlayer()
                ));
                gameActionJPanel.getHitJButton().getAction().setEnabled(blackjackEngine.getRuleset().isHittingPossible(
                    blackjackEngine.getActiveHandContext(),
                    blackjackEngine.getState()
                ));
                gameActionJPanel.getSplitJButton().getAction().setEnabled(blackjackEngine.getRuleset().isSplittingPossible(
                    blackjackEngine.getActiveHandContext(),
                    blackjackEngine.getState(),
                    blackjackEngine.getActiveHandContextIndex(),
                    blackjackEngine.getPlayer()
                ));
                gameActionJPanel.getStandJButton().getAction().setEnabled(true);
                gameActionJPanel.getSurrenderJButton().getAction().setEnabled(blackjackEngine.getRuleset().isSurrenderingPossible(
                    blackjackEngine.getActiveHandContext(),
                    blackjackEngine.getState()
                ));
            });
            case DEALER_TURN -> executorService.submit(() ->
            {
                blackjackEngine.dealerTurn();
                blackjackEngine.advanceAfterDealerTurn();
            });
            case SHOWDOWN -> executorService.submit(blackjackEngine::showdown);
        }
    }

    private void togglePlayerInputComponents(boolean enabled)
    {
        SwingUtilities.invokeLater(() ->
        {
            gameInfoJPanel.getPlayerInputJButton().setEnabled(enabled);
            gameInfoJPanel.getPlayerInputJTextField().setEnabled(enabled);
        });
    }

    private void updateUiAfterPlayerChipAmountChanges()
    {
        SwingUtilities.invokeLater(() ->
        {
            gameInfoJPanel.getPlayerChipAmountJLabel().setText(String.format(
                "%s $%,.2f",
                UiConstants.GAME_INFO_JPANEL_PLAYER_CHIP_AMOUNT_LABEL,
                blackjackEngine.getPlayer().getChips()
            ));
            gameInfoJPanel.getPlayerInputJTextField().setText("");
        });
        updateUiForEngineMessage();
    }

    private void updateUiForEngineMessage()
    {
        SwingUtilities.invokeLater(() -> gameInfoJPanel.getEngineMessageJTextArea().setCaretPosition(gameInfoJPanel.getEngineMessageJTextArea().getText().length()));
    }

    private void updateUiForPlayerScore()
    {
        SwingUtilities.invokeLater(() ->
        {
            gameInfoJPanel.getActiveHandContextHandScoreJLabel().setText(String.format(
                "%s %s",
                UiConstants.GAME_INFO_JPANEL_ACTIVE_HAND_CONTEXT_HAND_SCORE_LABEL,
                blackjackEngine.getActiveHandContext().getHand().getScore()
            ));
        });
    }

    private void renderCardForPlayer(Card card)
    {
        SwingUtilities.invokeLater(() ->
        {
            gameCardsJPanel.getActivePlayerHandJPanel().add(new CardJLabel(card, true));
            gameCardsJPanel.getActivePlayerHandJPanel().revalidate();
            gameCardsJPanel.getActivePlayerHandJPanel().repaint();
        });
    }
}