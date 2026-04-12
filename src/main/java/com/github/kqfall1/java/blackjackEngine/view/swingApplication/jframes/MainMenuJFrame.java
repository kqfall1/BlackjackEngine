package com.github.kqfall1.java.blackjackEngine.view.swingApplication.jframes;

import com.github.kqfall1.java.blackjackEngine.model.engine.BlackjackRulesetConfiguration;
import com.github.kqfall1.java.blackjackEngine.view.swingApplication.UiActions;
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
public final class MainMenuJFrame extends BlackjackJFrame
{
    public MainMenuJFrame()
    {
        final var panelWrapper = new JPanel(new GridBagLayout());
        panelWrapper.add(new MainMenuButtonJPanel(UiActions.getInstance()));
        panelWrapper.setOpaque(false);
        add(panelWrapper, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(MainMenuJFrame::new);
    }

    public void newGame(BlackjackRulesetConfiguration config)
    {
        new GameJFrame(config);
        setVisible(false);
    }
}