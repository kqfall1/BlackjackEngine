package com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents;

import com.github.kqfall1.java.blackjackEngine.view.swingApplication.ui.UiActions;
import com.github.kqfall1.java.blackjackEngine.view.swingApplication.ui.UiConstants;
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
        final var fileMenu = new JMenu(UiConstants.JMENU_FILE_LABEL);
        fileMenu.add(uiActions.getExit());
        fileMenu.add(uiActions.getNewGame());
        fileMenu.add(uiActions.getMainMenu());
        fileMenu.add(uiActions.getCredits());
        add(fileMenu);
    }
}