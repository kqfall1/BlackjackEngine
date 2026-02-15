package com.github.kqfall1.java.blackjackEngine.view.swingApplication;

import com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents.GameConfigJPanel;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

/**
 * Centralizes all Swing {@code Action} objects for all {@code JFrame} objects used by the Swing application.
 *
 * @author kqfall1
 * @since 25/01/2026
 */
public final class UiActions
{
    private final Action EXIT;
    private static UiActions instance;
    private final Action NEW_GAME;

    private UiActions()
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
                final var ROOT = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, (Component) e.getSource());
                final var CONFIG_DIALOG = new JDialog(ROOT, UiConstants.GAME_CONFIG_JDIALOG_TITLE, true);
                CONFIG_DIALOG.setContentPane(new GameConfigJPanel());
                CONFIG_DIALOG.setSize(UiConstants.getGameConfigJDialogDimension());
                CONFIG_DIALOG.setLocationRelativeTo(ROOT);
                CONFIG_DIALOG.setVisible(true);
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

    public static UiActions getInstance()
    {
        if (instance == null)
        {
            instance = new UiActions();
        }

        return instance;
    }

    public Action getNewGame()
    {
        return NEW_GAME;
    }

    public void setKeystrokes(JFrame jframe)
    {
        final var ACTION_MAP = jframe.getRootPane().getActionMap();
        final var INPUT_MAP = jframe.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        ACTION_MAP.put(UiConstants.JMENU_ITEM_EXIT_LABEL, getExit());
        INPUT_MAP.put(
            KeyStroke.getKeyStroke("ctrl E"),
            UiConstants.JMENU_ITEM_EXIT_LABEL
        );

        ACTION_MAP.put(UiConstants.JMENU_ITEM_NEW_GAME_LABEL, getNewGame());
        INPUT_MAP.put(
            KeyStroke.getKeyStroke("ctrl N"),
            UiConstants.JMENU_ITEM_NEW_GAME_LABEL
        );
    }
}