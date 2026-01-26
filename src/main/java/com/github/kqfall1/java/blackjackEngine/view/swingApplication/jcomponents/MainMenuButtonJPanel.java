package com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents;

import com.github.kqfall1.java.blackjackEngine.view.swingApplication.UiActions;
import java.awt.*;
import javax.swing.*;

/**
 * A {@code JPanel to contain all {@code JButton} objects required by a {@code MainMenuJFrame} object.
 *
 * @author kqfall1
 * @since 24/01/2026
 */
public final class MainMenuButtonJPanel extends JPanel
{
    private static final int JBUTTON_HEIGHT = 100;
    private static final int JBUTTON_WIDTH = 300;
    private static final int MARGIN_X = 25;
    private static final int MARGIN_Y = 25;

    public MainMenuButtonJPanel(UiActions uiActions)
    {
        setBackground(Color.LIGHT_GRAY);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(Box.createVerticalStrut(MARGIN_Y));
        final var NEW_GAME_BUTTON = new MainMenuJPanelJButton(uiActions.getNewGame());
        NEW_GAME_BUTTON.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(NEW_GAME_BUTTON);

        add(Box.createVerticalStrut(MARGIN_Y));
        final var EXIT_BUTTON = new MainMenuJPanelJButton(uiActions.getExit());
        EXIT_BUTTON.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(EXIT_BUTTON);

        add(Box.createVerticalStrut(MARGIN_Y));
    }

    @Override
    public Dimension getMaximumSize()
    {
        return getPreferredSize();
    }

    @Override
    public Dimension getPreferredSize()
    {
        int spaceBetweenButtons = MARGIN_Y * 2;

        for (int count = 0; count < getComponentCount(); count++)
        {
            spaceBetweenButtons += MARGIN_Y;
        }

        final int HEIGHT = getComponentCount() * JBUTTON_HEIGHT + spaceBetweenButtons;
        final int WIDTH = MARGIN_X * 2 + JBUTTON_WIDTH;
        return new Dimension(WIDTH, HEIGHT);
    }

    private static class MainMenuJPanelJButton extends JButton
    {
        private MainMenuJPanelJButton(Action action)
        {
            super(action);
            setBackground(Color.GREEN);
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