package com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents;

import com.github.kqfall1.java.blackjackEngine.view.swingApplication.UiActions;
import com.github.kqfall1.java.blackjackEngine.view.swingApplication.UiConstants;
import java.awt.*;
import javax.swing.*;

/**
 * Contains all {@code JButton} objects required by a {@code MainMenuJFrame} object.
 *
 * @author kqfall1
 * @since 24/01/2026
 */
public final class MainMenuButtonJPanel extends JPanel
{
    private static final int JBUTTON_HEIGHT = 100;
    private static final int JBUTTON_WIDTH = 300;

    public MainMenuButtonJPanel(UiActions uiActions)
    {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);

        add(Box.createVerticalStrut(UiConstants.MARGIN_SMALL));
        final var newGameButton = new MainMenuJPanelJButton(uiActions.getNewGame());
        newGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(newGameButton);

        add(Box.createVerticalStrut(UiConstants.MARGIN_SMALL));
        final var creditsButton = new MainMenuJPanelJButton(uiActions.getCredits());
        creditsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(creditsButton);

        add(Box.createVerticalStrut(UiConstants.MARGIN_SMALL));
        final var exitButton = new MainMenuJPanelJButton(uiActions.getExit());
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(exitButton);
    }

    private static class MainMenuJPanelJButton extends JButton
    {
        private MainMenuJPanelJButton(Action action)
        {
            super(action);
            setFont(UiConstants.JBUTTON_LARGE_FONT);
            setForeground(Color.BLACK);
        }

        @Override
        public Dimension getMaximumSize()
        {
            return getPreferredSize();
        }

        @Override
        public Dimension getPreferredSize()
        {
            return new Dimension(JBUTTON_WIDTH, JBUTTON_HEIGHT);
        }
    }
}