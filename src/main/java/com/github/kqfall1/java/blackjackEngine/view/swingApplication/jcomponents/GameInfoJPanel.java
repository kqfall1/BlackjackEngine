package com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents;

import com.github.kqfall1.java.blackjackEngine.view.swingApplication.ui.UiConstants;
import com.github.kqfall1.java.frameworks.awt.AwtUtils;
import com.github.kqfall1.java.frameworks.awt.swing.ValidatedJTextField;
import com.github.kqfall1.java.interfaces.FailurePresenter;
import java.awt.*;
import java.util.Optional;
import javax.swing.*;

/**
 * Contains all {@code JComponent} objects required to provide general information about the blackjack game.
 *
 * <p>This information includes the {@code Dealer}'s {@code Hand}'s score, the active {@code PlayerHand}'s {@code Hand}'s score,
 * and the {@code Player}'s chip count. A {@code JComponent} is provided to advance the game and provide {@code String} input.</p>
 *
 * @author kqfall1
 * @since 01/03/2026
 */
public final class GameInfoJPanel extends JPanel implements FailurePresenter
{
    private final JLabel activeHandContextHandScoreJLabel;
    private final JButton advanceEngineJButton;
    private final JLabel dealerHandScoreJLabel;
    private final JTextArea engineMessageJTextArea;
    private final JScrollPane engineMessageJScrollPane;
    private final JLabel playerChipAmountJLabel;
    private final ValidatedJTextField playerInputJTextField;
    private final JButton submitJButton;

    public GameInfoJPanel()
    {
        activeHandContextHandScoreJLabel = new JLabel(UiConstants.GAME_INFO_JPANEL_ACTIVE_HAND_CONTEXT_HAND_SCORE_LABEL);
        advanceEngineJButton = new JButton(UiConstants.GAME_INFO_JPANEL_ADVANCE_HAND_JBUTTON_LABEL);
        advanceEngineJButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        dealerHandScoreJLabel = new JLabel(UiConstants.GAME_INFO_JPANEL_DEALER_HAND_SCORE_LABEL);
        engineMessageJTextArea = new JTextArea();
        engineMessageJTextArea.setEditable(false);
        engineMessageJTextArea.setLineWrap(true);
        engineMessageJTextArea.setMargin(UiConstants.JTEXT_AREA_INSETS);
        engineMessageJScrollPane = new JScrollPane(engineMessageJTextArea);
        engineMessageJScrollPane.setPreferredSize(new Dimension(
            UiConstants.GAME_INFO_JPANEL_ENGINE_JSCROLL_PANEL_WIDTH,
            UiConstants.GAME_INFO_JPANEL_ENGINE_JSCROLL_PANEL_HEIGHT
        ));
        engineMessageJScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        playerChipAmountJLabel = new JLabel(UiConstants.GAME_INFO_JPANEL_PLAYER_CHIP_AMOUNT_LABEL);
        playerInputJTextField = new ValidatedJTextField();
        playerInputJTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
        playerInputJTextField.setEnabled(false);
        playerInputJTextField.setHorizontalAlignment(JTextField.CENTER);
        submitJButton = new JButton(UiConstants.GAME_ACTION_SUBMIT_LABEL);
        submitJButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitJButton.setEnabled(false);

        final var gameInfoWrapper = new JPanel();
        gameInfoWrapper.setLayout(new BoxLayout(gameInfoWrapper, BoxLayout.Y_AXIS));
        gameInfoWrapper.add(dealerHandScoreJLabel);
        gameInfoWrapper.add(Box.createVerticalStrut(UiConstants.MARGIN_MEDIUM));
        gameInfoWrapper.add(playerChipAmountJLabel);
        gameInfoWrapper.add(Box.createVerticalStrut(UiConstants.MARGIN_SMALL));
        gameInfoWrapper.add(activeHandContextHandScoreJLabel);
        gameInfoWrapper.add(Box.createVerticalStrut(UiConstants.MARGIN_MEDIUM));

        final var gameInputWrapper = new JPanel();
        gameInputWrapper.setLayout(new BoxLayout(gameInputWrapper, BoxLayout.Y_AXIS));
        gameInputWrapper.add(Box.createVerticalStrut(UiConstants.MARGIN_SMALL));
        gameInputWrapper.add(playerInputJTextField);
        gameInputWrapper.add(Box.createVerticalStrut(UiConstants.MARGIN_EXTRA_SMALL));
        gameInputWrapper.add(submitJButton);
        gameInputWrapper.add(Box.createVerticalStrut(UiConstants.MARGIN_SMALL));
        gameInputWrapper.add(advanceEngineJButton);
        gameInputWrapper.add(Box.createVerticalStrut(UiConstants.MARGIN_EXTRA_SMALL));

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(Box.createVerticalStrut(UiConstants.MARGIN_SMALL));
        add(gameInfoWrapper);
        add(gameInputWrapper);
        add(engineMessageJScrollPane);

        for (Component jLabel : AwtUtils.getNestedComponents(Optional.of(JLabel.class), this))
        {
            jLabel.setFont(UiConstants.JLABEL_LARGE_FONT);
        }
    }

    public JLabel getActiveHandContextHandScoreJLabel()
    {
        return activeHandContextHandScoreJLabel;
    }

    public JButton getAdvanceEngineJButton()
    {
        return advanceEngineJButton;
    }

    public JLabel getDealerHandScoreJLabel()
    {
        return dealerHandScoreJLabel;
    }

    public JTextArea getEngineMessageJTextArea()
    {
        return engineMessageJTextArea;
    }

    public JLabel getPlayerChipAmountJLabel()
    {
        return playerChipAmountJLabel;
    }

    public JButton getSubmitJButton()
    {
        return submitJButton;
    }

    public ValidatedJTextField getPlayerInputJTextField()
    {
        return playerInputJTextField;
    }

    @Override
    public void presentFailure(String message, Component... components)
    {
        SwingUtilities.invokeLater(() ->
        {
            engineMessageJTextArea.append(String.format("%s\n\n", message));

            for (final var component : components)
            {
                ((JComponent) component).setBorder(UiConstants.BORDER_RED);
            }

            new Timer(UiConstants.SLEEP_INTERVAL, event ->
            {
                for (final var component : components)
                {
                    final var jComponent = (JComponent) component;

                    if (jComponent instanceof JTextField)
                    {
                        jComponent.setBorder(new JTextField().getBorder());
                    }
                    else if (jComponent instanceof JButton)
                    {
                        jComponent.setBorder(new JButton().getBorder());
                    }
                    else
                    {
                        jComponent.setBorder(null);
                    }
                }

                ((Timer) event.getSource()).stop();
            }).start();
        });
    }
}