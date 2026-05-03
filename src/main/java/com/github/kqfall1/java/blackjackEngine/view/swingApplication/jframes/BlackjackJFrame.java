package com.github.kqfall1.java.blackjackEngine.view.swingApplication.jframes;

import com.github.kqfall1.java.blackjackEngine.view.swingApplication.ui.UiActions;
import com.github.kqfall1.java.blackjackEngine.view.swingApplication.ui.UiConstants;
import static com.github.kqfall1.java.blackjackEngine.view.swingApplication.ui.UiConstants.*;
import java.awt.*;
import javax.swing.*;

/**
 * Provides abstraction for the basic configuration of {@code JFrame} objects used for this blackjack app.
 *
 * @author kqfall1
 * @since 08/02/2026
 */
public abstract class BlackjackJFrame extends JFrame
{
    public BlackjackJFrame()
    {
        final var iconImageHeight = 32;
        final var iconImageWidth = 32;
        final var uiActions = UiActions.getInstance();
        final var background = new JPanel()
        {
            @Override
            public void paintComponent(Graphics g)
            {
                super.paintComponent(g);
                g.drawImage(UiConstants.JFRAME_BACKGROUND.getImage(), 0, 0, null);
            }
        };
        background.setLayout(new BorderLayout());
        setContentPane(background);
        setIconImage(BLACKJACK_JFRAME_LOGO.getImage().getScaledInstance(iconImageWidth, iconImageHeight, Image.SCALE_SMOOTH));
        setJMenuBar(new ApplicationJMenuBar(uiActions));
        uiActions.setKeystrokes(this);
        setResizable(false);
        setSize(BLACKJACK_JFRAME_DIMENSION);
        setTitle(BLACKJACK_JFRAME_TITLE);
    }

    private static class ApplicationJMenuBar extends JMenuBar
    {
        public ApplicationJMenuBar(UiActions uiActions)
        {
            final var fileMenu = new JMenu(UiConstants.JMENU_FILE_LABEL);
            fileMenu.add(uiActions.getExit());
            fileMenu.add(uiActions.getNewGame());
            fileMenu.add(uiActions.getMainMenu());
            fileMenu.add(uiActions.getCredits());
            add(fileMenu);
        }
    }
}