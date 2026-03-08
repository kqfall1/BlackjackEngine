package com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents;

import com.github.kqfall1.java.blackjackEngine.model.engine.BlackjackEngine;
import com.github.kqfall1.java.blackjackEngine.model.enums.EngineState;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.InsufficientChipsException;
import com.github.kqfall1.java.blackjackEngine.model.exceptions.RuleViolationException;
import com.github.kqfall1.java.blackjackEngine.view.swingApplication.UiConstants;
import com.github.kqfall1.java.enums.YesNoInput;
import com.github.kqfall1.java.interfaces.FailurePresenter;
import com.github.kqfall1.java.javax.swing.AwtUtils;
import java.awt.*;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import javax.swing.*;

/**
 * Contains all {@code JComponent} objects required to provide general information about the blackjack game.
 *
 * <p>This information includes the {@code Dealer}'s {@code Hand}'s score, the active {@code PlayerHand}'s {@code Hand}'s score,
 * and the {@code Player}'s chip count. {@code JComponent} objects are provided to advance the game and provide {@code String} input.</p>
 *
 * @author kqfall1
 * @since 01/03/2026
 */
public final class GameInfoJPanel extends JPanel implements FailurePresenter
{
    private final JLabel activeHandContextHandScoreJLabel;
    private final JButton advanceEngineJButton;
    private final BlackjackEngine blackjackEngine;
    private final JLabel dealerHandScoreJLabel;
    private final JTextArea engineMessageJTextArea;
    private final JScrollPane engineMessageJScrollPane;
    private final ExecutorService executorService;
    private final JLabel playerChipAmountJLabel;
    private final JButton playerInputJButton;
    private final JLabel playerInputJLabel;
    private final JTextField playerInputJTextField;
    private static final int PLAYER_INPUT_JTEXTFIELD_WIDTH = 10;

    public GameInfoJPanel(BlackjackEngine blackjackEngine, ExecutorService executorService)
    {
        activeHandContextHandScoreJLabel = new JLabel(UiConstants.GAME_ACTIVE_HAND_CONTEXT_HAND_SCORE_LABEL);
        advanceEngineJButton = new JButton(UiConstants.GAME_ADVANCE_HAND_JBUTTON_LABEL);
        this.blackjackEngine = blackjackEngine;
        dealerHandScoreJLabel = new JLabel(UiConstants.GAME_DEALER_HAND_SCORE_LABEL);
        dealerHandScoreJLabel.setFont(UiConstants.JBUTTON_FONT);
        engineMessageJTextArea = new JTextArea();
        engineMessageJTextArea.setEditable(false);
        engineMessageJTextArea.setLineWrap(true);
        engineMessageJScrollPane = new JScrollPane(engineMessageJTextArea);
        engineMessageJScrollPane.setPreferredSize(new Dimension(
            UiConstants.GAME_ENGINE_JSCROLL_PANEL_WIDTH,
            UiConstants.GAME_ENGINE_JSCROLL_PANEL_HEIGHT
        ));
        engineMessageJScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.executorService = executorService;
        playerChipAmountJLabel = new JLabel(UiConstants.GAME_PLAYER_CHIP_AMOUNT_LABEL);
        playerInputJButton = new JButton(UiConstants.GAME_PLAYER_INPUT_JBUTTON_LABEL);
        playerInputJButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        playerInputJButton.setEnabled(false);
        playerInputJLabel = new JLabel();
        playerInputJLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        playerInputJTextField = new JTextField(PLAYER_INPUT_JTEXTFIELD_WIDTH);
        playerInputJTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
        playerInputJTextField.setHorizontalAlignment(JTextField.CENTER);

        advanceEngineJButton.addActionListener(e ->
        {
            if (blackjackEngine.getState() == EngineState.START)
            {
                executorService.submit(blackjackEngine::start);
            }
            //will continue and add more states
        });
        playerInputJButton.addActionListener(e ->
        {
            if (blackjackEngine.getState() == EngineState.BETTING)
            {
                final var INPUT = playerInputJTextField.getText().trim();

                try
                {
                    final var PARSED_INPUT = Double.parseDouble(INPUT);

                    CompletableFuture.runAsync(() ->
                    {
                        try
                        {
                            blackjackEngine.placeBet(BigDecimal.valueOf(PARSED_INPUT));
                            blackjackEngine.deal();
                            blackjackEngine.advanceAfterDeal();
                        }
                        catch (InsufficientChipsException ex)
                        {
                            presentFailure(ex.getMessage(), playerChipAmountJLabel);
                        }
                        catch (RuleViolationException ex)
                        {
                            presentFailure(ex.getMessage(), playerInputJButton);
                        }
                    }, executorService);
                }
                catch (IllegalArgumentException | NullPointerException ex)
                {
                    presentFailure(INPUT, playerInputJTextField);
                }
            }
            else if (blackjackEngine.getState() == EngineState.INSURANCE_CHECK)
            {
                final var INPUT = playerInputJTextField.getText().trim();

                try
                {
                    final var PARSED_INPUT = YesNoInput.valueOf(INPUT);

                    if (PARSED_INPUT == YesNoInput.YES)
                    {
                        CompletableFuture.supplyAsync(blackjackEngine::acceptInsuranceBet, executorService)
                            .thenAccept(blackjackEngine::advanceAfterInsuranceBet);
                    }
                    else
                    {
                        executorService.submit(blackjackEngine::declineInsuranceBet);
                    }
                }
                catch (IllegalArgumentException ex)
                {
                    presentFailure(INPUT, playerInputJTextField);
                }
            }
        });

        final var GAME_INFO_WRAPPER = new JPanel();
        GAME_INFO_WRAPPER.setLayout(new BoxLayout(GAME_INFO_WRAPPER, BoxLayout.Y_AXIS));
        GAME_INFO_WRAPPER.add(dealerHandScoreJLabel);
        GAME_INFO_WRAPPER.add(Box.createVerticalStrut(UiConstants.MARGIN_MEDIUM));
        GAME_INFO_WRAPPER.add(playerChipAmountJLabel);
        GAME_INFO_WRAPPER.add(Box.createVerticalStrut(UiConstants.MARGIN_SMALL));
        GAME_INFO_WRAPPER.add(activeHandContextHandScoreJLabel);
        GAME_INFO_WRAPPER.add(Box.createVerticalStrut(UiConstants.MARGIN_MEDIUM));

        final var GAME_INPUT_WRAPPER = new JPanel();
        GAME_INPUT_WRAPPER.setLayout(new BoxLayout(GAME_INPUT_WRAPPER, BoxLayout.Y_AXIS));
        GAME_INPUT_WRAPPER.add(playerInputJLabel);
        GAME_INPUT_WRAPPER.add(Box.createVerticalStrut(UiConstants.MARGIN_SMALL));
        GAME_INPUT_WRAPPER.add(playerInputJTextField);
        GAME_INPUT_WRAPPER.add(Box.createVerticalStrut(UiConstants.MARGIN_EXTRA_SMALL));
        GAME_INPUT_WRAPPER.add(playerInputJButton);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(Box.createVerticalStrut(UiConstants.MARGIN_SMALL));
        add(GAME_INFO_WRAPPER);
        add(GAME_INPUT_WRAPPER);
        add(Box.createVerticalStrut(UiConstants.MARGIN_SMALL));
        add(advanceEngineJButton);
        add(Box.createVerticalStrut(UiConstants.MARGIN_EXTRA_SMALL));
        add(engineMessageJScrollPane);

        for (Component component : AwtUtils.getNestedComponents(this))
        {
            if (component instanceof JLabel jlabel)
            {
                jlabel.setFont(UiConstants.JLABEL_FONT);
            }
        }
    }

    public JButton getAdvanceEngineJButton()
    {
        return advanceEngineJButton;
    }

    public JButton getPlayerInputJButton()
    {
        return playerInputJButton;
    }

    @Override
    public void presentFailure(String message, Component... components)
    {
        SwingUtilities.invokeLater(() ->
        {
            final var DEFAULT_JTEXT_FIELD_BORDER = new JTextField().getBorder();
            engineMessageJTextArea.append(String.format("%s\n", message));

            for (Component component : components)
            {
                final var JCOMPONENT = (JComponent) component;
                JCOMPONENT.setBorder(UiConstants.BORDER_RED);
            }

            new Timer(UiConstants.SLEEP_INTERVAL, event ->
            {
                for (Component component : components)
                {
                    final var JCOMPONENT = (JComponent) component;
                    JCOMPONENT.setBorder(DEFAULT_JTEXT_FIELD_BORDER);
                }

                ((Timer) event.getSource()).stop();
            }).start();
        });
    }
}