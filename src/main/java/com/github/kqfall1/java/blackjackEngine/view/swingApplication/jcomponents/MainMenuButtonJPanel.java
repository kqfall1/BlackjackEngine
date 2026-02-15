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

        add(Box.createVerticalStrut(UiConstants.DEFAULT_MARGIN_VALUE));
        final var NEW_GAME_BUTTON = new MainMenuJPanelJButton(uiActions.getNewGame());
        NEW_GAME_BUTTON.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(NEW_GAME_BUTTON);

        add(Box.createVerticalStrut(UiConstants.DEFAULT_MARGIN_VALUE));
        final var EXIT_BUTTON = new MainMenuJPanelJButton(uiActions.getExit());
        EXIT_BUTTON.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(EXIT_BUTTON);

        add(Box.createVerticalStrut(UiConstants.DEFAULT_MARGIN_VALUE));
    }

    private static class MainMenuJPanelJButton extends JButton
    {
        private MainMenuJPanelJButton(Action action)
        {
            super(action);
            setBackground(UiConstants.JBUTTON_BACKGROUND_COLOR);
            setFont(UiConstants.JBUTTON_FONT);
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