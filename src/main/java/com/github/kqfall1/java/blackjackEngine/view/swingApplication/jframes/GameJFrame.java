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
        gameCardsJPanel = new GameCardsJPanel();
        gameInfoJPanel = new GameInfoJPanel();
        this.mainMenuJFrame = mainMenuJFrame;

        final var ACTION_MAP = getRootPane().getActionMap();
        final var INPUT_MAP = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        blackjackEngine = new BlackjackEngine(
            this,
            UiConstants.BLACKJACK_ENGINE_LOG_FILE_PATH,
            UiConstants.BLACKJACK_ENGINE_LOGGER_NAME,
            new StandardBlackjackRuleset(config)
        );
        executorService = Executors.newSingleThreadExecutor();

        final var ADVANCE = UiActions.getInstance().getGameAction(e ->
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
            else if (blackjackEngine.getState() == BlackjackEngineState.SHOWING_DOWN)
            {
                executorService.submit(blackjackEngine::showdown);
            }
            else if (blackjackEngine.getState() == BlackjackEngineState.SHOWING_DOWN_FINAL_HAND)
            {
                executorService.submit(() ->
                {
                    blackjackEngine.advanceAfterShowdown();
                    blackjackEngine.reset();
                    blackjackEngine.advanceAfterReset();
                });
            }
        }, UiConstants.GAME_ACTION_ADVANCE_LABEL, ACTION_MAP, INPUT_MAP, KeyStroke.getKeyStroke("SPACE"));
        final var DOUBLE_DOWN = UiActions.getInstance().getGameAction(e ->
        {
            disableGameActionJPanelJButtons();
            executorService.submit(blackjackEngine::playerDoubleDown);
            updateUiForPlayerChipAmount();
        }, UiConstants.GAME_ACTION_DOUBLE_DOWN_LABEL, ACTION_MAP, INPUT_MAP, KeyStroke.getKeyStroke("D"));
        final var HIT = UiActions.getInstance().getGameAction(e ->
        {
            disableGameActionJPanelJButtons();
            executorService.submit(blackjackEngine::playerHit);
        }, UiConstants.GAME_ACTION_HIT_LABEL, ACTION_MAP, INPUT_MAP, KeyStroke.getKeyStroke("H"));
        final var SPLIT = UiActions.getInstance().getGameAction(e ->
        {
            disableGameActionJPanelJButtons();
            executorService.submit(blackjackEngine::playerSplit);
            updateUiForPlayerChipAmount();
        }, UiConstants.GAME_ACTION_SPLIT_LABEL, ACTION_MAP, INPUT_MAP, KeyStroke.getKeyStroke("P"));
        final var STAND = UiActions.getInstance().getGameAction(e ->
        {
            disableGameActionJPanelJButtons();
            executorService.submit(blackjackEngine::playerStand);
        }, UiConstants.GAME_ACTION_STAND_LABEL, ACTION_MAP, INPUT_MAP, KeyStroke.getKeyStroke("T"));
        final var SUBMIT = UiActions.getInstance().getGameAction(e ->
        {
            toggleGameInfoJPanelPlayerInputComponents(false);

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
                                    toggleGameInfoJPanelPlayerInputComponents(true);
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
                        toggleGameInfoJPanelPlayerInputComponents(true);
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
                            CompletableFuture.supplyAsync(blackjackEngine::acceptInsuranceBet, executorService).thenAccept(blackjackEngine::advanceAfterInsuranceBet);
                        }
                        else
                        {
                            executorService.submit(blackjackEngine::declineInsuranceBet);
                            SwingUtilities.invokeLater(() -> gameInfoJPanel.getPlayerInputJTextField().setText(""));
                        }
                    }
                    else
                    {
                        toggleGameInfoJPanelPlayerInputComponents(true);
                        gameInfoJPanel.presentFailure(throwable.getMessage(), gameInfoJPanel.getPlayerInputJTextField());
                    }
                });
            }
        }, UiConstants.GAME_ACTION_SUBMIT_LABEL, ACTION_MAP, INPUT_MAP, KeyStroke.getKeyStroke(UiConstants.GAME_ACTION_ENTER_KEYSTROKE_LABEL));
        final var SURRENDER = UiActions.getInstance().getGameAction(e ->
        {
            disableGameActionJPanelJButtons();
            executorService.submit(blackjackEngine::playerSurrender);
        }, UiConstants.GAME_ACTION_SURRENDER_LABEL, ACTION_MAP, INPUT_MAP, KeyStroke.getKeyStroke("X"));

        gameActionJPanel = new GameActionJPanel(DOUBLE_DOWN, HIT, SPLIT, STAND, SURRENDER);
        gameInfoJPanel.getAdvanceEngineJButton().setAction(ADVANCE);
        gameInfoJPanel.getAdvanceEngineJButton().getAction().setEnabled(true);
        gameInfoJPanel.getPlayerInputJTextField().addActionListener(SUBMIT);
        gameInfoJPanel.getSubmitJButton().setAction(SUBMIT);
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

    private void disableGameActionJPanelJButtons()
    {
        SwingUtilities.invokeLater(() ->
        {
            for (Component component : AwtUtils.getNestedComponents(gameActionJPanel))
            {
                if (component instanceof JButton jbutton)
                {
                    jbutton.getAction().setEnabled(false);
                }
            }
        });
    }

    @Override
    public void onBetPlaced(HandContext handContext)
    {
        gameInfoJPanel.getEngineMessageJTextArea().append(String.format(
            "%s%.2f.\n\n", UiConstants.GAME_MESSAGE_BET_PLACED, handContext.getBet().getAmount().doubleValue()
        ));
        updateUiForPlayerChipAmount();
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
        updateUiForPlayerScore(handContext);
    }

    @Override
    public void onDrawingRoundCompletedDealer(Hand dealerHand) {}

    @Override
    public void onDrawingRoundCompletedPlayer(HandContext handContext)
    {
        disableGameActionJPanelJButtons();
        gameInfoJPanel.getAdvanceEngineJButton().getAction().setEnabled(true);
        gameInfoJPanel.getAdvanceEngineJButton().setEnabled(true);
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

        updateUiForPlayerScore(handContext);
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
        updateUiForPlayerChipAmount();
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
        updateUiForPlayerChipAmount();
    }

    @Override
    public void onPlayerSplit(HandContext currentHand, HandContext splitHand)
    {
        SwingUtilities.invokeLater(() ->
        {
            gameInfoJPanel.getEngineMessageJTextArea().append(String.format("%s\n\n", UiConstants.GAME_MESSAGE_SPLIT));
            gameCardsJPanel.getActivePlayerHandJPanel().remove(BlackjackConstants.INITIAL_CARD_COUNT - 1);
        });
        renderCardForPlayer(currentHand.getHand().getCards().getLast());
        updateUiForEngineMessage();
        updateUiForPlayerChipAmount();
        updateUiForPlayerScore(currentHand);
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
        clearActivePlayerHandJPanel();
        handContext.getHand().getCards().forEach(this::renderCardForPlayer);
        updateUiForPlayerScore(handContext);

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
            gameInfoJPanel.getAdvanceEngineJButton().getAction().setEnabled(true);
            gameInfoJPanel.getEngineMessageJTextArea().append(String.format(
                "%s%.2f.\n\n", UiConstants.GAME_MESSAGE_SHOWDOWN_COLLECTION, playerWinnings
            ));
            gameInfoJPanel.getDealerHandScoreJLabel().setText(String.format(
                "%s%d", UiConstants.GAME_INFO_JPANEL_DEALER_HAND_SCORE_LABEL, dealerHand.getScore()
            ));
        });
        updateUiForPlayerChipAmount();
    }

    @Override
    public void onShowdownStarted(Hand dealerHand, HandContext handContext)
    {
        SwingUtilities.invokeLater(() ->
        {
            gameInfoJPanel.getAdvanceEngineJButton().getAction().setEnabled(false);
            ((CardJLabel) gameCardsJPanel.getDealerHandJPanel().getComponents()[BlackjackConstants.INITIAL_CARD_COUNT - 1]).setFaceUp(true);
        });
    }

    @Override
    public void onStateChanged(BlackjackEngineState oldState)
    {
        switch (blackjackEngine.getState())
        {
            case BETTING, INSURANCE_CHECK -> toggleGameInfoJPanelPlayerInputComponents(true);
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
            case SHOWING_DOWN ->
            {
                if (oldState != BlackjackEngineState.SHOWING_DOWN)
                {
                    executorService.submit(blackjackEngine::showdown);
                }
            }
        }
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

    private void toggleGameInfoJPanelPlayerInputComponents(boolean enabled)
    {
        SwingUtilities.invokeLater(() ->
        {
            gameInfoJPanel.getPlayerInputJTextField().setEnabled(enabled);

            if (enabled)
            {
                gameInfoJPanel.getPlayerInputJTextField().requestFocusInWindow();
            }

            gameInfoJPanel.getSubmitJButton().setEnabled(enabled);
        });
    }

    private void updateUiForEngineMessage()
    {
        SwingUtilities.invokeLater(() -> gameInfoJPanel.getEngineMessageJTextArea().setCaretPosition(gameInfoJPanel.getEngineMessageJTextArea().getText().length()));
    }

    private void updateUiForPlayerChipAmount()
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

    private void updateUiForPlayerScore(HandContext handContext)
    {
        SwingUtilities.invokeLater(() ->
        {
            gameInfoJPanel.getActiveHandContextHandScoreJLabel().setText(String.format(
                "%s %s",
                UiConstants.GAME_INFO_JPANEL_ACTIVE_HAND_CONTEXT_HAND_SCORE_LABEL,
                handContext.getHand().getScore()
            ));
        });
    }
}