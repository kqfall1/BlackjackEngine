package com.github.kqfall1.java.blackjackEngine.view.swingApplication.jframes;

import static com.github.kqfall1.java.blackjackEngine.view.swingApplication.SwingApplicationConstants.*;
import com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents.MainMenuBackground;
import com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents.MainMenuButtonJpanel;
import java.awt.*;
import javax.swing.*;

/**
 * Allows players to choose a high-level operation of the application to be executed (ie, starting a new blackjack game,
 * exiting the application, configuring settings, etc.).
 *
 * @author kqfall1
 * @since 24/01/2026
 */
public final class MainMenuFrame extends JFrame
{
    public static void main(String[] args)
    {
        EventQueue.invokeLater(() -> new MainMenuFrame().setVisible(true));
    }

    public MainMenuFrame()
    {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setSize(JFRAME_DIMENSION);
        setResizable(false);
        setTitle(JFRAME_TITLE);

        final var BACKGROUND = new MainMenuBackground();
        BACKGROUND.setBounds(0, 0, JFRAME_DIMENSION.width, JFRAME_DIMENSION.height);
        add(BACKGROUND);

        final var BUTTON_PANEL = new MainMenuButtonJpanel();
        final var BUTTON_PANEL_SIZE = BUTTON_PANEL.getPreferredSize();
        final var COORDINATE_DIVISOR = 4;
        BUTTON_PANEL.setBounds(
             JFRAME_DIMENSION.width / COORDINATE_DIVISOR,
             JFRAME_DIMENSION.height / COORDINATE_DIVISOR,
             BUTTON_PANEL_SIZE.width,
             BUTTON_PANEL_SIZE.height
       );
        add(BUTTON_PANEL);
    }
}