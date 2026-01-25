package com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents;

import java.awt.*;
import javax.swing.*;

public final class MainMenuButtonJpanel extends JPanel
{
    private static final int JBUTTON_HEIGHT = 100;
    private static final int JBUTTON_WIDTH = 300;
    private static final int MARGIN_X = 25;
    private static final int MARGIN_Y = 25;

    public MainMenuButtonJpanel()
    {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(Box.createVerticalStrut(MARGIN_Y));
        final var NEW_GAME_BUTTON = new MainMenuButton("New Game");
        add(NEW_GAME_BUTTON);

        add(Box.createVerticalStrut(MARGIN_Y));
        final var EXIT_BUTTON = new MainMenuButton("Exit");
        add(EXIT_BUTTON);

        add(Box.createVerticalStrut(MARGIN_Y));
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

    private static class MainMenuButton extends JButton
    {
        private MainMenuButton(String text)
        {
            super(text);
            setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        @Override
        public Dimension getMaximumSize()
        {
            return new Dimension(JBUTTON_WIDTH, JBUTTON_HEIGHT);
        }

        @Override
        public Dimension getPreferredSize()
        {
            return new Dimension(JBUTTON_WIDTH, JBUTTON_HEIGHT);
        }
    }
}