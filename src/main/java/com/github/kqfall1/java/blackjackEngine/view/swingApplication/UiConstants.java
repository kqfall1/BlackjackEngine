package com.github.kqfall1.java.blackjackEngine.view.swingApplication;

import java.awt.*;
import javax.swing.*;

/**
 * A static class that holds simple. constant values required for the Swing user interface.
 *
 * @author kqfall1
 * @since 24/01/2026
 */
public final class UiConstants
{
    public static final Dimension JFRAME_DIMENSION = Toolkit.getDefaultToolkit().getScreenSize();
    public static final ImageIcon JFRAME_LOGO
            = new ImageIcon(UiConstants.class.getResource("/images/logo.jpg"));
    public static final String JFRAME_TITLE = "Blackjack";
    public static final String JMENU_FILE_LABEL = "File";
    public static final String JMENU_ITEM_EXIT_LABEL = "Exit";
    public static final String JMENU_ITEM_NEW_GAME_LABEL = "New Game";
    public static final ImageIcon MAIN_MENU_BACKGROUND
            = new ImageIcon(UiConstants.class.getResource("/images/MainMenuBackground.jpg"));

    private UiConstants() {}
}