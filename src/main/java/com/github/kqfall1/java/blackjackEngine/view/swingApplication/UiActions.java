package com.github.kqfall1.java.blackjackEngine.view.swingApplication;

import com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents.GameConfigJPanel;
import com.github.kqfall1.java.blackjackEngine.view.swingApplication.jframes.MainMenuJFrame;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;
import javax.swing.*;

/**
 * Centralizes common Swing {@code Action} objects for all {@code JFrame} objects used by the Swing application.
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
                JFrame root;

                if (e.getSource() instanceof JMenuItem)
                {
                    root = (JFrame) SwingUtilities.getWindowAncestor(((JPopupMenu) ((JMenuItem) e.getSource()).getParent()).getInvoker());
                }
                else
                {
                    root = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, (Component) e.getSource());
                }

                final var CONFIG_DIALOG = new JDialog(root, UiConstants.GAME_CONFIG_JDIALOG_TITLE, true);
                CONFIG_DIALOG.setContentPane(new GameConfigJPanel(root));
                CONFIG_DIALOG.setSize(UiConstants.getGameConfigJDialogDimension());
                CONFIG_DIALOG.setLocationRelativeTo(root);
                CONFIG_DIALOG.setVisible(true);
            }
        };

        newGame.putValue(Action.LONG_DESCRIPTION, UiConstants.JMENU_ITEM_NEW_GAME_LABEL);
        newGame.putValue(Action.NAME, UiConstants.JMENU_ITEM_NEW_GAME_LABEL);
        newGame.putValue(Action.SHORT_DESCRIPTION, UiConstants.JMENU_ITEM_NEW_GAME_LABEL);
    }

    public Action getGameAction(Consumer<ActionEvent> actionConsumer, String actionLabel, ActionMap actionMap, InputMap inputMap,
                                KeyStroke keyStroke)
    {
        final var ACTION = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                actionConsumer.accept(e);
            }
        };
        ACTION.putValue(Action.LONG_DESCRIPTION, actionLabel);
        ACTION.putValue(Action.NAME, actionLabel);
        ACTION.putValue(Action.SHORT_DESCRIPTION, actionLabel);
        ACTION.setEnabled(false);
        actionMap.put(actionLabel, ACTION);
        inputMap.put(keyStroke, actionLabel);
        return ACTION;
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
        ACTION_MAP.put(UiConstants.JMENU_ITEM_NEW_GAME_LABEL, getNewGame());
        INPUT_MAP.put(KeyStroke.getKeyStroke("ctrl E"), UiConstants.JMENU_ITEM_EXIT_LABEL);
        INPUT_MAP.put(KeyStroke.getKeyStroke("ctrl N"), UiConstants.JMENU_ITEM_NEW_GAME_LABEL);
    }
}