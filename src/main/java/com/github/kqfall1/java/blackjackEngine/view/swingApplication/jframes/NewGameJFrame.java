package com.github.kqfall1.java.blackjackEngine.view.swingApplication.jframes;

import com.github.kqfall1.java.blackjackEngine.view.swingApplication.UiActions;
import com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents.ApplicationJMenuBar;
import static com.github.kqfall1.java.blackjackEngine.view.swingApplication.UiConstants.*;
import java.awt.*;
import javax.swing.*;

/**
 * Allows players to configure settings before launching a new game.
 *
 * @author kqfall1
 * @since 08/02/2026
 */
public final class NewGameJFrame extends JFrame
{
    public NewGameJFrame()
    {
        final var UI_ACTIONS = UiActions.getInstance();

        setJMenuBar(new ApplicationJMenuBar(UI_ACTIONS));
        setSize(JFRAME_DIMENSION);
        setVisible(true);
    }
}