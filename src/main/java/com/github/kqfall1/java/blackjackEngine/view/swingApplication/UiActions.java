package com.github.kqfall1.java.blackjackEngine.view.swingApplication;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Is instantiated by a {@code MainMenuJFrame} and centralizes all Swing {@code Action} objects for all
 * {@code JFrame} objects used by the Swing application.
 *
 * @author kqfall1
 * @since 25/01/2026
 */
public final class UiActions
{
    private final Action EXIT;
    private final Action NEW_GAME;

    public UiActions()
    {
        EXIT = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                System.exit(0);
            }
        };

        EXIT.putValue(Action.LONG_DESCRIPTION, UiConstants.JMENU_ITEM_EXIT_LABEL);
        EXIT.putValue(Action.NAME, UiConstants.JMENU_ITEM_EXIT_LABEL);
        EXIT.putValue(Action.SHORT_DESCRIPTION, UiConstants.JMENU_ITEM_EXIT_LABEL);

        NEW_GAME = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {

            }
        };

        NEW_GAME.putValue(Action.LONG_DESCRIPTION, UiConstants.JMENU_ITEM_NEW_GAME_LABEL);
        NEW_GAME.putValue(Action.NAME, UiConstants.JMENU_ITEM_NEW_GAME_LABEL);
        NEW_GAME.putValue(Action.SHORT_DESCRIPTION, UiConstants.JMENU_ITEM_NEW_GAME_LABEL);
    }

    public Action getExit()
    {
        return EXIT;
    }

    public Action getNewGame()
    {
        return NEW_GAME;
    }
}