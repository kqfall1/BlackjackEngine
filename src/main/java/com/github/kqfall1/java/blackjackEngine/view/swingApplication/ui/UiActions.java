package com.github.kqfall1.java.blackjackEngine.view.swingApplication.ui;

import com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents.CreditsJPanel;
import com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents.GameConfigJPanel;
import com.github.kqfall1.java.blackjackEngine.view.swingApplication.jframes.GameJFrame;
import com.github.kqfall1.java.blackjackEngine.view.swingApplication.jframes.MainMenuJFrame;
import com.github.kqfall1.java.frameworks.awt.AwtUtils;
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
    private final Action credits;
    private final Action exit;
    private static UiActions instance;
    private final Action mainMenu;
    private final Action newGame;

    private UiActions()
    {
        credits = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                final var optionalWindow = AwtUtils.getRootWindow(e);
                optionalWindow.ifPresent(window ->
                {
                    final var configDialog = new JDialog(window, UiConstants.GAME_ACTION_CREDITS_LABEL, Dialog.ModalityType.MODELESS);
                    configDialog.setContentPane(new CreditsJPanel());
                    configDialog.setSize(UiConstants.GAME_JDIALOG_DIMENSION);
                    configDialog.setLocationRelativeTo(window);
                    configDialog.setVisible(true);
                });
            }
        };
        credits.putValue(Action.LONG_DESCRIPTION, UiConstants.GAME_ACTION_CREDITS_LABEL);
        credits.putValue(Action.NAME, UiConstants.GAME_ACTION_CREDITS_LABEL);
        credits.putValue(Action.SHORT_DESCRIPTION, UiConstants.GAME_ACTION_CREDITS_LABEL);

        exit = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (JOptionPane.showConfirmDialog((Component) e.getSource(), String.format("%s?", UiConstants.GAME_ACTION_EXIT_LABEL)) == JOptionPane.YES_OPTION)
                {
                    System.exit(0);
                }
            }
        };
        exit.putValue(Action.LONG_DESCRIPTION, UiConstants.GAME_ACTION_EXIT_LABEL);
        exit.putValue(Action.NAME, UiConstants.GAME_ACTION_EXIT_LABEL);
        exit.putValue(Action.SHORT_DESCRIPTION, UiConstants.GAME_ACTION_EXIT_LABEL);

        mainMenu = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                final var optionalWindow = AwtUtils.getRootWindow(e);
                optionalWindow.ifPresent(window ->
                {
                    if (!(window instanceof MainMenuJFrame))
                    {
                        if (window instanceof GameJFrame gameJFrame)
                        {
                            if (JOptionPane.showConfirmDialog(window, String.format("%s?", UiConstants.GAME_MESSAGE_MAIN_MENU_WARNING)) == JOptionPane.YES_OPTION)
                            {
                                gameJFrame.getExecutorService().shutdown();
                            }
                            else
                            {
                                return;
                            }
                        }

                        new MainMenuJFrame();
                        window.dispose();
                    }
                });
            }
        };
        mainMenu.putValue(Action.LONG_DESCRIPTION, UiConstants.GAME_ACTION_MAIN_MENU_LABEL);
        mainMenu.putValue(Action.NAME, UiConstants.GAME_ACTION_MAIN_MENU_LABEL);
        mainMenu.putValue(Action.SHORT_DESCRIPTION, UiConstants.GAME_ACTION_MAIN_MENU_LABEL);

        newGame = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                final var optionalWindow = AwtUtils.getRootWindow(e);
                optionalWindow.ifPresent(window ->
                {
                    if (window instanceof JFrame windowJFrame)
                    {
                        final var configDialog = new JDialog(windowJFrame, UiConstants.GAME_CONFIG_JDIALOG_TITLE, true);
                        configDialog.setContentPane(new GameConfigJPanel(windowJFrame));
                        configDialog.setSize(UiConstants.GAME_JDIALOG_DIMENSION);
                        configDialog.setLocationRelativeTo(window);
                        configDialog.setVisible(true);
                    }
                });
            }
        };
        newGame.putValue(Action.LONG_DESCRIPTION, UiConstants.GAME_ACTION_NEW_GAME_LABEL);
        newGame.putValue(Action.NAME, UiConstants.GAME_ACTION_NEW_GAME_LABEL);
        newGame.putValue(Action.SHORT_DESCRIPTION, UiConstants.GAME_ACTION_NEW_GAME_LABEL);
    }

    public Action getGameAction(Consumer<ActionEvent> actionConsumer, String actionLabel, ActionMap actionMap, InputMap inputMap,
                                KeyStroke keyStroke)
    {
        final var action = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                actionConsumer.accept(e);
            }
        };
        action.putValue(Action.LONG_DESCRIPTION, actionLabel);
        action.putValue(Action.NAME, actionLabel);
        action.putValue(Action.SHORT_DESCRIPTION, actionLabel);
        action.setEnabled(false);
        actionMap.put(actionLabel, action);
        inputMap.put(keyStroke, actionLabel);
        return action;
    }

    public Action getCredits()
    {
        return credits;
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

    public Action getMainMenu()
    {
        return mainMenu;
    }

    public Action getNewGame()
    {
        return newGame;
    }

    public void setKeystrokes(JFrame jframe)
    {
        final var actionMap = jframe.getRootPane().getActionMap();
        final var inputMap = jframe.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        actionMap.put(UiConstants.GAME_ACTION_CREDITS_LABEL, getCredits());
        actionMap.put(UiConstants.GAME_ACTION_EXIT_LABEL, getExit());
        actionMap.put(UiConstants.GAME_ACTION_MAIN_MENU_LABEL, getMainMenu());
        actionMap.put(UiConstants.GAME_ACTION_NEW_GAME_LABEL, getNewGame());
        inputMap.put(KeyStroke.getKeyStroke("ctrl C"), UiConstants.GAME_ACTION_CREDITS_LABEL);
        inputMap.put(KeyStroke.getKeyStroke("ctrl E"), UiConstants.GAME_ACTION_EXIT_LABEL);
        inputMap.put(KeyStroke.getKeyStroke("ctrl M"), UiConstants.GAME_ACTION_MAIN_MENU_LABEL);
        inputMap.put(KeyStroke.getKeyStroke("ctrl N"), UiConstants.GAME_ACTION_NEW_GAME_LABEL);
    }
}