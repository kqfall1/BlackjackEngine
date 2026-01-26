package com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents;

import com.github.kqfall1.java.blackjackEngine.view.swingApplication.UiActions;
import com.github.kqfall1.java.blackjackEngine.view.swingApplication.UiConstants;
import javax.swing.*;

/**
 * A {@code JMenuBar} to be used for all {@code JFrame} objects for the Swing user interface.
 *
 * @author kqfall1
 * @since 25/01/2026
 */
public class ApplicationJMenuBar extends JMenuBar
{
    public ApplicationJMenuBar(UiActions uiActions)
    {
        final var FILE_MENU = new JMenu(UiConstants.JMENU_FILE_LABEL);
        FILE_MENU.add(uiActions.getExit());
        FILE_MENU.add(uiActions.getNewGame());
        add(FILE_MENU);
    }
}