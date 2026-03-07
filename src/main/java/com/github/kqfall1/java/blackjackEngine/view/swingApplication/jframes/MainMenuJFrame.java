package com.github.kqfall1.java.blackjackEngine.view.swingApplication.jframes;

import com.github.kqfall1.java.blackjackEngine.model.engine.BlackjackRulesetConfiguration;
import com.github.kqfall1.java.blackjackEngine.view.swingApplication.UiActions;
import com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents.MainMenuButtonJPanel;
import java.awt.*;
import java.math.BigDecimal;
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
        final var PANEL_WRAPPER = new JPanel(new GridBagLayout());
        PANEL_WRAPPER.add(new MainMenuButtonJPanel(UiActions.getInstance()));
        PANEL_WRAPPER.setOpaque(false);
        add(PANEL_WRAPPER, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args)
    {
        //SwingUtilities.invokeLater(MainMenuJFrame::new);
        SwingUtilities.invokeLater(() ->
        {
            final var CONFIG = new BlackjackRulesetConfiguration();
            CONFIG.setPlayerInitialChips(BigDecimal.valueOf(5000));
            final var MAIN_MENU_JFRAME = new MainMenuJFrame();
            MAIN_MENU_JFRAME.newGame(CONFIG);
        });
    }

    public void newGame(BlackjackRulesetConfiguration config)
    {
        new GameJFrame(config, this);
        setVisible(false);
    }
}