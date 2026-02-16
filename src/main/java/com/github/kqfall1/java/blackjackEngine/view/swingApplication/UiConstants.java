package com.github.kqfall1.java.blackjackEngine.view.swingApplication;

import com.github.kqfall1.java.blackjackEngine.model.cards.Shoe;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

/**
 * A static class that holds simple, constant values required for the Swing user interface.
 *
 * @author kqfall1
 * @since 24/01/2026
 */
public final class UiConstants
{
    public static final Dimension BLACKJACK_JFRAME_DIMENSION = Toolkit.getDefaultToolkit().getScreenSize();
    public static final ImageIcon BLACKJACK_JFRAME_LOGO = new ImageIcon(UiConstants.class.getResource("/images/logo.jpg"));
    public static final String BLACKJACK_JFRAME_TITLE = "Blackjack";
    public static final Border BORDER_RED = BorderFactory.createLineBorder(Color.RED);
    public static final int DEFAULT_GRID_BAG_LAYOUT_WEIGHT = 100;
    public static final int DEFAULT_MARGIN_VALUE = 25;
    public static final String DOUBLING_DOWN_ON_SPLIT_HANDS_ALLOWED_LABEL = "Is doubling down on split hands allowed?";
    public static final String GAME_CONFIG_JDIALOG_FAILURE_LABEL = "One or more inputs is invalid.";
    public static final double GAME_CONFIG_JDIALOG_HEIGHT_FACTOR = 0.4;
    public static final String GAME_CONFIG_JDIALOG_TITLE = "Game Configuration";
    public static final double GAME_CONFIG_JDIALOG_WIDTH_FACTOR = 0.5;
    public static final Color JBUTTON_BACKGROUND_COLOR = new Color(238, 220, 130);
    public static final Font JBUTTON_FONT = new Font("Rockwell", Font.BOLD, 18);
    public static final String JMENU_FILE_LABEL = "File";
    public static final String JMENU_ITEM_EXIT_LABEL = "Exit";
    public static final String JMENU_ITEM_NEW_GAME_LABEL = "New Game";
    public static final ImageIcon MAIN_MENU_BACKGROUND = new ImageIcon(UiConstants.class.getResource("/images/background.jpg"));
    public static final String MAXIMUM_SPLIT_COUNT_LABEL = "Maximum split count:";
    public static final String MINIMUM_BET_AMOUNT_LABEL = "Minimum bet amount:";
    public static final String PLAY_BUTTON_LABEL = "Play!";
    public static final String PLAYER_INITIAL_CHIPS_LABEL = "Player initial chips:";
    public static final String SHOE_DECK_COUNT_LABEL = "Shoe deck count:";
    public static final String SHOE_PENETRATION_LABEL = String.format(
        "Shoe penetration (%.1f-%.1f):",
        Shoe.MINIMUM_PENETRATION,
        Shoe.MAXIMUM_PENETRATION
    );
    public static final String SHOULD_DEALER_HIT_ON_SOFT_17_LABEL = "Should dealer hit on soft 17?";
    public static final int SLEEP_INTERVAL_LONG = 1000;
    public static final String SPLITTING_ACES_ALLOWED_LABEL = "Is splitting aces allowed?";
    public static final String SURRENDERING_ALLOWED_LABEL = "Is surrendering allowed?";
    public static final String SURRENDERING_ON_SPLIT_HANDS_ALLOWED_LABEL = "Is surrendering on split hands allowed?";

    private UiConstants() {}

    public static Dimension getGameConfigJDialogDimension()
    {
        return new Dimension(
            (int) (BLACKJACK_JFRAME_DIMENSION.width * GAME_CONFIG_JDIALOG_WIDTH_FACTOR),
            (int) (BLACKJACK_JFRAME_DIMENSION.height * GAME_CONFIG_JDIALOG_HEIGHT_FACTOR)
        );
    }
}