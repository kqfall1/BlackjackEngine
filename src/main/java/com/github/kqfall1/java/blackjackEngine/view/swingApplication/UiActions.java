package com.github.kqfall1.java.blackjackEngine.view.swingApplication;

import com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents.GameConfigJPanel;
import com.github.kqfall1.java.blackjackEngine.view.swingApplication.jframes.MainMenuJFrame;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 * Centralizes all Swing {@code Action} objects for all {@code JFrame} objects used by the Swing application.
 *
 * @author kqfall1
 * @since 25/01/2026
 */
public final class UiActions
{
    private final Action exit;
    private static UiActions instance;
    private final Action newGame;

    private UiActions()
    {
        exit = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                System.exit(0);
            }
        };

        exit.putValue(Action.LONG_DESCRIPTION, UiConstants.JMENU_ITEM_EXIT_LABEL);
        exit.putValue(Action.NAME, UiConstants.JMENU_ITEM_EXIT_LABEL);
        exit.putValue(Action.SHORT_DESCRIPTION, UiConstants.JMENU_ITEM_EXIT_LABEL);

        newGame = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                final var ROOT = (MainMenuJFrame) SwingUtilities.getAncestorOfClass(Frame.class, (Component) e.getSource());
                final var CONFIG_DIALOG = new JDialog(ROOT, UiConstants.GAME_CONFIG_JDIALOG_TITLE, true);
                CONFIG_DIALOG.setContentPane(new GameConfigJPanel(ROOT));
                CONFIG_DIALOG.setSize(UiConstants.getGameConfigJDialogDimension());
                CONFIG_DIALOG.setLocationRelativeTo(ROOT);
                CONFIG_DIALOG.setVisible(true);
            }
        };

        newGame.putValue(Action.LONG_DESCRIPTION, UiConstants.JMENU_ITEM_NEW_GAME_LABEL);
        newGame.putValue(Action.NAME, UiConstants.JMENU_ITEM_NEW_GAME_LABEL);
        newGame.putValue(Action.SHORT_DESCRIPTION, UiConstants.JMENU_ITEM_NEW_GAME_LABEL);
    }

    public Action getExit()
    {
        return exit;
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
        return newGame;
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