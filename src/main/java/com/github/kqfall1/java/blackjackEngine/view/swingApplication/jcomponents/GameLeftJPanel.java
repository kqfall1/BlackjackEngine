package com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents;

import com.github.kqfall1.java.blackjackEngine.view.swingApplication.UiConstants;
import javax.swing.*;

/**
 * Contains all {@code JComponent} objects required to provide general information about the blackjack game.
 *
 * <p>This information includes the {@code Dealer}'s {@code Hand}'s score, the active {@code PlayerHand}'s {@code Hand}'s score,
 * and the {@code Player}'s chip count. {@code JComponent} objects are provided to advance the game and provide {@code String} input.</p>
 */
public final class GameLeftJPanel extends JPanel
{
    private final JLabel dealerHandScoreJLabel;

    public GameLeftJPanel()
    {
        dealerHandScoreJLabel = new JLabel(UiConstants.GAME_DEALER_HAND_SCORE_LABEL);
        dealerHandScoreJLabel.setFont(UiConstants.JBUTTON_FONT);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(Box.createVerticalStrut(UiConstants.DEFAULT_MARGIN_VALUE));
        add(dealerHandScoreJLabel);
        add(Box.createVerticalStrut(UiConstants.DEFAULT_MARGIN_VALUE));
    }
}