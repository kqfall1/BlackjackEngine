package com.github.kqfall1.java.blackjackEngine.view.swingApplication.jframes;

import com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents.ConfigurationJPanel;
import java.awt.*;

/**
 * Allows players to configure settings before launching a new blackjack game.
 *
 * @author kqfall1
 * @since 08/02/2026
 */
public final class NewGameJFrame extends BlackjackJFrame
{
    public NewGameJFrame()
    {
        add(new ConfigurationJPanel(), BorderLayout.WEST);
        setVisible(true);
    }
}