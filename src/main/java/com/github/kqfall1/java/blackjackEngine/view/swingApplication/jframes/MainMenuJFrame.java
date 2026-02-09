package com.github.kqfall1.java.blackjackEngine.view.swingApplication.jframes;

import static com.github.kqfall1.java.blackjackEngine.view.swingApplication.UiConstants.*;
import com.github.kqfall1.java.blackjackEngine.view.swingApplication.UiActions;
import com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents.ApplicationJMenuBar;
import com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents.ApplicationBackground;
import com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents.MainMenuButtonJPanel;
import java.awt.*;
import javax.swing.*;

/**
 * Allows players to choose a high-level operation of the application to be executed (ie, starting a new blackjack game,
 * exiting the application, etc.).
 *
 * @author kqfall1
 * @since 24/01/2026
 */
public final class MainMenuJFrame extends JFrame
{
    public static void main(String[] args)
    {
        EventQueue.invokeLater(MainMenuJFrame::new);
    }

    public MainMenuJFrame()
    {
        final var ICON_IMAGE_HEIGHT = 32;
        final var ICON_IMAGE_WIDTH = 32;
        final var UI_ACTIONS = UiActions.getInstance();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(
            JFRAME_LOGO.getImage().getScaledInstance(
                ICON_IMAGE_WIDTH,
                ICON_IMAGE_HEIGHT,
                Image.SCALE_SMOOTH
            )
        );
        UI_ACTIONS.setKeystrokes(this);
        setSize(JFRAME_DIMENSION);
        setResizable(false);
        setTitle(JFRAME_TITLE);

        setJMenuBar(new ApplicationJMenuBar(UI_ACTIONS));

        final var BACKGROUND = new ApplicationBackground();
        BACKGROUND.setLayout(new BorderLayout());
        setContentPane(BACKGROUND);

        final var PANEL_WRAPPER = new JPanel(new GridBagLayout());
        PANEL_WRAPPER.add(new MainMenuButtonJPanel(UI_ACTIONS));
        PANEL_WRAPPER.setOpaque(false);
        add(PANEL_WRAPPER, BorderLayout.CENTER);

        setVisible(true);
    }
}