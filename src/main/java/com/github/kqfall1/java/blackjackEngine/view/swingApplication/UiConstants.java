package com.github.kqfall1.java.blackjackEngine.view.swingApplication;

import java.awt.*;
import javax.swing.*;

/**
 * A static class that holds simple, constant values required for the Swing user interface.
 *
 * @author kqfall1
 * @since 24/01/2026
 */
public final class UiConstants
{
    public static final Dimension BLACKJACK_JFRAME_DIMENSION = Toolkit.getDefaultToolkit().getScreenSize();
    public static final ImageIcon BLACKJACK_JFRAME_LOGO
        = new ImageIcon(UiConstants.class.getResource("/images/logo.jpg"));
    public static final String BLACKJACK_JFRAME_TITLE = "Blackjack";
    public static final double GAME_CONFIG_JDIALOG_HEIGHT_FACTOR = 0.75;
    public static final String GAME_CONFIG_JDIALOG_TITLE = "Game Configuration";
    public static final double GAME_CONFIG_JDIALOG_WIDTH_FACTOR = 0.5;
    public static final Color JBUTTON_BACKGROUND_COLOR = new Color(238, 220, 130);
    public static final Font JBUTTON_FONT = new Font("Rockwell", Font.BOLD, 18) ;
    public static final String JMENU_FILE_LABEL = "File";
    public static final String JMENU_ITEM_EXIT_LABEL = "Exit";
    public static final String JMENU_ITEM_NEW_GAME_LABEL = "New Game";
    public static final ImageIcon MAIN_MENU_BACKGROUND
        = new ImageIcon(UiConstants.class.getResource("/images/background.jpg"));

    private UiConstants() {}

    public static Dimension getGameConfigJDialogDimension()
    {
        return new Dimension(
            (int) (BLACKJACK_JFRAME_DIMENSION.width * GAME_CONFIG_JDIALOG_WIDTH_FACTOR),
            (int) (BLACKJACK_JFRAME_DIMENSION.height * GAME_CONFIG_JDIALOG_HEIGHT_FACTOR)
        );
    }
}