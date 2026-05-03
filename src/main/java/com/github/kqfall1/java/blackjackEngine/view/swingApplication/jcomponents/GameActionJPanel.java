package com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents;

import com.github.kqfall1.java.blackjackEngine.view.swingApplication.ui.UiConstants;
import java.awt.*;
import javax.swing.*;

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

    public GameActionJPanel(Action doubleDown, Action hit, Action split, Action stand, Action surrender)
    {
        doubleDownJButton = new JButton(doubleDown);
        doubleDownJButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        hitJButton = new JButton(hit);
        hitJButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        splitJButton = new JButton(split);
        splitJButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        standJButton = new JButton(stand);
        standJButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        surrenderJButton = new JButton(surrender);
        surrenderJButton.setAlignmentX(Component.CENTER_ALIGNMENT);

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