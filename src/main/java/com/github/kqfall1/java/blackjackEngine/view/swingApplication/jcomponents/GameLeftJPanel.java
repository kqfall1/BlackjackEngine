package com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents;

import com.github.kqfall1.java.blackjackEngine.view.swingApplication.UiConstants;
import com.github.kqfall1.java.javax.swing.AwtUtils;
import javax.swing.*;
import java.awt.*;

/**
 * Contains all {@code JComponent} objects required to provide general information about the blackjack game.
 *
 * <p>This information includes the {@code Dealer}'s {@code Hand}'s score, the active {@code PlayerHand}'s {@code Hand}'s score,
 * and the {@code Player}'s chip count. {@code JComponent} objects are provided to advance the game and provide {@code String} input.</p>
 *
 * @author kqfall1
 * @since 01/03/2026
 */
public final class GameLeftJPanel extends JPanel
{
    private final JLabel activeHandContextHandScoreJLabel;
    private final JButton advanceEngineJButton;
    private final JLabel dealerHandScoreJLabel;
    private final JTextArea engineMessageJTextArea;
    private final JScrollPane engineMessageJScrollPane;
    private final JLabel playerChipAmountJLabel;
    private final JButton playerInputJButton;
    private final JLabel playerInputJLabel;
    private final JTextField playerInputJTextField;
    private static final int PLAYER_INPUT_JTEXTFIELD_WIDTH = 10;

    public GameLeftJPanel()
    {
        activeHandContextHandScoreJLabel = new JLabel(UiConstants.GAME_ACTIVE_HAND_CONTEXT_HAND_SCORE_LABEL);
        advanceEngineJButton = new JButton(UiConstants.GAME_ADVANCE_HAND_JBUTTON_LABEL);
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
        playerChipAmountJLabel = new JLabel(UiConstants.GAME_PLAYER_CHIP_AMOUNT_LABEL);
        playerInputJButton = new JButton(UiConstants.GAME_PLAYER_INPUT_JBUTTON_LABEL);
        playerInputJLabel = new JLabel();
        playerInputJTextField = new JTextField(PLAYER_INPUT_JTEXTFIELD_WIDTH);
        playerInputJTextField.setHorizontalAlignment(JTextField.CENTER);

        final var TEXT_FIELD_WRAPPER = new JPanel();
        TEXT_FIELD_WRAPPER.setLayout(new FlowLayout(FlowLayout.CENTER));
        TEXT_FIELD_WRAPPER.add(playerInputJTextField);

        final var PLAYER_INPUT_WRAPPER = new JPanel();
        PLAYER_INPUT_WRAPPER.setLayout(new BoxLayout(PLAYER_INPUT_WRAPPER, BoxLayout.Y_AXIS));
        PLAYER_INPUT_WRAPPER.add(playerInputJLabel);
        PLAYER_INPUT_WRAPPER.add(TEXT_FIELD_WRAPPER);
        PLAYER_INPUT_WRAPPER.add(playerInputJButton);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(Box.createVerticalStrut(UiConstants.DEFAULT_MARGIN_VALUE));
        add(dealerHandScoreJLabel);
        add(Box.createVerticalStrut(UiConstants.GAME_ENTITY_JLABEL_SECTION_MARGIN));
        add(playerChipAmountJLabel);
        add(Box.createVerticalStrut(UiConstants.DEFAULT_MARGIN_VALUE));
        add(activeHandContextHandScoreJLabel);
        add(Box.createVerticalStrut(UiConstants.GAME_ENTITY_JLABEL_SECTION_MARGIN));
        add(PLAYER_INPUT_WRAPPER);
        add(Box.createVerticalStrut(UiConstants.DEFAULT_MARGIN_VALUE));
        add(advanceEngineJButton);
        add(engineMessageJScrollPane);

        for (Component component : AwtUtils.getNestedComponents(this))
        {
            if (component instanceof JLabel jlabel)
            {
                jlabel.setFont(UiConstants.JLABEL_FONT);
            }
        }
    }
}