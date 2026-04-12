package com.github.kqfall1.java.blackjackEngine.view.swingApplication;

import com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents.GameConfigJPanel;
import com.github.kqfall1.java.frameworks.awt.swing.SwingUtils;
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
                final var optionalRootJFrame = SwingUtils.getRootJFrame(e);
                optionalRootJFrame.ifPresent(rootJFrame ->
                {
                    final var configDialog = new JDialog(rootJFrame, UiConstants.GAME_CONFIG_JDIALOG_TITLE, true);
                    configDialog.setContentPane(new GameConfigJPanel(rootJFrame));
                    configDialog.setSize(UiConstants.getGameConfigJDialogDimension());
                    configDialog.setLocationRelativeTo(rootJFrame);
                    configDialog.setVisible(true);
                });
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
        final var actionMap = jframe.getRootPane().getActionMap();
        final var inputMap = jframe.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        actionMap.put(UiConstants.JMENU_ITEM_EXIT_LABEL, getExit());
        actionMap.put(UiConstants.JMENU_ITEM_NEW_GAME_LABEL, getNewGame());
        inputMap.put(KeyStroke.getKeyStroke("ctrl E"), UiConstants.JMENU_ITEM_EXIT_LABEL);
        inputMap.put(KeyStroke.getKeyStroke("ctrl N"), UiConstants.JMENU_ITEM_NEW_GAME_LABEL);
    }
}