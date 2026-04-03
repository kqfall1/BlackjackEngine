package com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents;

import com.github.kqfall1.java.blackjackEngine.view.swingApplication.UiConstants;
import com.github.kqfall1.java.interfaces.FailurePresenter;
import com.github.kqfall1.java.javax.swing.AwtUtils;
import com.github.kqfall1.java.javax.swing.ValidatedJTextField;
import java.awt.*;
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

        final var ENGINE_MESSAGE_JSCROLL_PANE_INPUT_MAP = engineMessageJScrollPane.getInputMap();
        ENGINE_MESSAGE_JSCROLL_PANE_INPUT_MAP.put(KeyStroke.getKeyStroke("ctrl H"), "");

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
        GAME_INPUT_WRAPPER.add(Box.createVerticalStrut(UiConstants.MARGIN_SMALL));
        GAME_INPUT_WRAPPER.add(playerInputJTextField);
        GAME_INPUT_WRAPPER.add(Box.createVerticalStrut(UiConstants.MARGIN_EXTRA_SMALL));
        GAME_INPUT_WRAPPER.add(submitJButton);
        GAME_INPUT_WRAPPER.add(Box.createVerticalStrut(UiConstants.MARGIN_SMALL));
        GAME_INPUT_WRAPPER.add(advanceEngineJButton);
        GAME_INPUT_WRAPPER.add(Box.createVerticalStrut(UiConstants.MARGIN_EXTRA_SMALL));

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(Box.createVerticalStrut(UiConstants.MARGIN_SMALL));
        add(GAME_INFO_WRAPPER);
        add(GAME_INPUT_WRAPPER);
        add(engineMessageJScrollPane);

        for (Component component : AwtUtils.getNestedComponents(this))
        {
            if (component instanceof JLabel jlabel)
            {
                jlabel.setFont(UiConstants.JLABEL_LARGE_FONT);
            }
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
            final var DEFAULT_JBUTTON_BORDER = new JButton().getBorder();
            final var DEFAULT_JTEXT_FIELD_BORDER = new JTextField().getBorder();
            engineMessageJTextArea.append(String.format("%s\n\n", message));

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

                    if (JCOMPONENT instanceof JTextField)
                    {
                        JCOMPONENT.setBorder(DEFAULT_JTEXT_FIELD_BORDER);
                    }
                    else if (JCOMPONENT instanceof JButton)
                    {
                        JCOMPONENT.setBorder(DEFAULT_JBUTTON_BORDER);
                    }
                    else
                    {
                        JCOMPONENT.setBorder(null);
                    }
                }

                ((Timer) event.getSource()).stop();
            }).start();
        });
    }
}