package com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents;

import com.github.kqfall1.java.blackjackEngine.view.swingApplication.UiConstants;
import javax.swing.*;
import java.awt.*;

/**
 * Contains all {@code JComponent} objects required to perform core blackjack actions.
 *
 * @author kqfall1
 * @since 03/03/2026
 */
public class GameActionJPanel extends JPanel
{
    private final JButton doubleDownJButton;
    private final JButton hitJButton;
    private final JButton splitJButton;
    private final JButton standJButton;
    private final JButton surrenderJButton;

    public GameActionJPanel()
    {
        doubleDownJButton = new JButton(UiConstants.GAME_JBUTTON_DOUBLE_DOWN_LABEL);
        doubleDownJButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        doubleDownJButton.setEnabled(false);
        hitJButton = new JButton(UiConstants.GAME_JBUTTON_HIT_LABEL);
        hitJButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        hitJButton.setEnabled(false);
        splitJButton = new JButton(UiConstants.GAME_JBUTTON_SPLIT_LABEL);
        splitJButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        splitJButton.setEnabled(false);
        standJButton = new JButton(UiConstants.GAME_JBUTTON_STAND_LABEL);
        standJButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        standJButton.setEnabled(false);
        surrenderJButton = new JButton(UiConstants.GAME_JBUTTON_SURRENDER_LABEL);
        surrenderJButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        surrenderJButton.setEnabled(false);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(Box.createVerticalStrut(UiConstants.MARGIN_LARGE));
        add(doubleDownJButton);
        add(Box.createVerticalStrut(UiConstants.MARGIN_EXTRA_SMALL));
        add(hitJButton);
        add(Box.createVerticalStrut(UiConstants.MARGIN_EXTRA_SMALL));
        add(splitJButton);
        add(Box.createVerticalStrut(UiConstants.MARGIN_EXTRA_SMALL));
        add(standJButton);
        add(Box.createVerticalStrut(UiConstants.MARGIN_EXTRA_SMALL));
        add(surrenderJButton);
    }

    public JButton getDoubleDownJButton()
    {
        return doubleDownJButton;
    }

    public JButton getHitJButton()
    {
        return hitJButton;
    }

    public JButton getSplitJButton()
    {
        return splitJButton;
    }

    public JButton getStandJButton()
    {
        return standJButton;
    }

    public JButton getSurrenderJButton()
    {
        return surrenderJButton;
    }
}