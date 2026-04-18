package com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents;

import com.github.kqfall1.java.blackjackEngine.view.swingApplication.UiConstants;
import java.awt.*;
import javax.swing.*;

/**
 * Contains two {@code JPanel} objects to display cards to the player.
 *
 * @author kqfall1
 * @since 07/03/2025
 */
public final class GameCardsJPanel extends JPanel
{
    private final JPanel activePlayerHandJPanel;
    private final JPanel dealerHandJPanel;

    public GameCardsJPanel()
    {
        activePlayerHandJPanel = new JPanel();
        activePlayerHandJPanel.setLayout(new FlowLayout(FlowLayout.LEFT, UiConstants.MARGIN_EXTRA_SMALL, UiConstants.MARGIN_SMALL));
        activePlayerHandJPanel.setOpaque(false);
        dealerHandJPanel = new JPanel();
        dealerHandJPanel.setLayout(new FlowLayout(FlowLayout.LEFT, UiConstants.MARGIN_EXTRA_SMALL, UiConstants.MARGIN_EXTRA_SMALL));
        dealerHandJPanel.setOpaque(false);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        add(Box.createVerticalStrut(UiConstants.MARGIN_SMALL));
        add(dealerHandJPanel);
        add(Box.createVerticalStrut(UiConstants.MARGIN_LARGE));
        add(activePlayerHandJPanel);
        add(Box.createVerticalStrut(UiConstants.MARGIN_SMALL));
    }

    public JPanel getActivePlayerHandJPanel()
    {
        return activePlayerHandJPanel;
    }

    public JPanel getDealerHandJPanel()
    {
        return dealerHandJPanel;
    }
}