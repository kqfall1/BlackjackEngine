package com.github.kqfall1.java.blackjackEngine.view.swingApplication;

import com.github.kqfall1.java.blackjackEngine.model.cards.Shoe;
import java.awt.*;
import java.util.prefs.Preferences;
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
    public static final String SWING_APPLICATION_PACKAGE_NAME = "com.github.kqfall1.java.blackjackEngine.view.swingApplication";
    public static final String BACK_OF_CARD_IMAGE_FILE_NAME = "back_of_card.png";
    public static final String BLACKJACK_ENGINE_LOGGER_NAME = String.format("%s%s", SWING_APPLICATION_PACKAGE_NAME, ".BlackjackEngine");
    public static final Dimension BLACKJACK_JFRAME_DIMENSION = Toolkit.getDefaultToolkit().getScreenSize();
    public static final ImageIcon BLACKJACK_JFRAME_LOGO = new ImageIcon(UiConstants.class.getResource("/images/logo.jpg"));
    public static final String BLACKJACK_JFRAME_TITLE = "Blackjack";
    public static final Border BORDER_RED = BorderFactory.createLineBorder(Color.RED);
    public static final int CARD_IMAGE_HEIGHT = 225;
    public static final int CARD_IMAGE_WIDTH = 100;
    public static final int DEFAULT_GRID_BAG_LAYOUT_WEIGHT = 100;
    public static final String GAME_ACTION_ADVANCE_LABEL = "Advance";
    public static final String GAME_ACTION_DOUBLE_DOWN_LABEL = "Double Down";
    public static final String GAME_ACTION_ENTER_KEYSTROKE_LABEL = "ENTER";
    public static final String GAME_ACTION_HIT_LABEL = "Hit";
    public static final String GAME_ACTION_SPLIT_LABEL = "Split";
    public static final String GAME_ACTION_STAND_LABEL = "Stand";
    public static final String GAME_ACTION_SUBMIT_LABEL = "Submit";
    public static final String GAME_ACTION_SURRENDER_LABEL = "Surrender";
    public static final String GAME_CONFIG_JDIALOG_DOUBLING_DOWN_ON_SPLIT_HANDS_ALLOWED_LABEL = "Is doubling down on split hands allowed?";
    public static final String GAME_CONFIG_JDIALOG_FAILURE_LABEL = "One or more inputs is invalid.";
    public static final double GAME_CONFIG_JDIALOG_HEIGHT_FACTOR = 0.4;
    public static final String GAME_CONFIG_JDIALOG_LOGGING_ENABLED_LABEL = "Is logging enabled?";
    public static final String GAME_CONFIG_JDIALOG_MAXIMUM_SPLIT_COUNT_LABEL = "Maximum split count:";
    public static final String GAME_CONFIG_JDIALOG_MINIMUM_BET_AMOUNT_LABEL = "Minimum bet amount:";
    public static final String GAME_CONFIG_JDIALOG_PLAY_BUTTON_LABEL = "Play!";
    public static final String GAME_CONFIG_JDIALOG_PLAYER_INITIAL_CHIPS_LABEL = "Player initial chips:";
    public static final String GAME_CONFIG_JDIALOG_TITLE = "Game Configuration";
    public static final String GAME_CONFIG_JDIALOG_SHOE_DECK_COUNT_LABEL = "Shoe deck count:";
    public static final String GAME_CONFIG_JDIALOG_SHOE_PENETRATION_LABEL = String.format(
        "Shoe penetration (%.1f-%.1f)",
        Shoe.MINIMUM_PENETRATION,
        Shoe.MAXIMUM_PENETRATION
    );
    public static final String GAME_CONFIG_JDIALOG_SHOULD_DEALER_HIT_ON_SOFT_17_LABEL = "Should dealer hit on soft 17?";
    public static final String GAME_CONFIG_JDIALOG_SPLITTING_ACES_ALLOWED_LABEL = "Is splitting aces allowed?";
    public static final String GAME_CONFIG_JDIALOG_SURRENDERING_ALLOWED_LABEL = "Is surrendering allowed?";
    public static final double GAME_CONFIG_JDIALOG_WIDTH_FACTOR = 0.5;
    public static final String GAME_INFO_JPANEL_ACTIVE_HAND_CONTEXT_HAND_SCORE_LABEL = "Current score: ";
    public static final String GAME_INFO_JPANEL_ADVANCE_HAND_JBUTTON_LABEL = "Advance";
    public static final String GAME_INFO_JPANEL_DEALER_HAND_SCORE_LABEL = "Dealer's score: ";
    public static final int GAME_INFO_JPANEL_ENGINE_JSCROLL_PANEL_HEIGHT = 475;
    public static final int GAME_INFO_JPANEL_ENGINE_JSCROLL_PANEL_WIDTH = 100;
    public static final String GAME_INFO_JPANEL_PLAYER_CHIP_AMOUNT_LABEL = "Player's chips: ";
    public static final String GAME_MESSAGE_BET_PLACED = "You have placed a bet of ";
    public static final String GAME_MESSAGE_BUSTED = "You have busted. Better luck next time!";
    public static final String GAME_MESSAGE_INSURANCE_BET_LOST = "You have lost your insurance bet.";
    public static final String GAME_MESSAGE_INSURANCE_BET_NOTIFICATION = "You are eligible to place an insurance bet. Please enter \"yes\" or \"no\".";
    public static final String GAME_MESSAGE_INSURANCE_BET_WON = "You have won your insurance bet and collect $";
    public static final String GAME_MESSAGE_SHOWDOWN_COLLECTION = "You have collected $";
    public static final String GAME_MESSAGE_SHOWDOWN_WON = "You have won a showdown!";
    public static final String GAME_MESSAGE_SPLIT = "You have elected to split.";
    public static final Font JBUTTON_LARGE_FONT = new Font("Rockwell", Font.BOLD, 18);
    public static final ImageIcon JFRAME_BACKGROUND = new ImageIcon(UiConstants.class.getResource("/images/background.jpg"));
    public static final Font JLABEL_LARGE_FONT = new Font("Georgia", Font.PLAIN, 16);
    public static final String JMENU_FILE_LABEL = "File";
    public static final String JMENU_ITEM_EXIT_LABEL = "Exit";
    public static final String JMENU_ITEM_MAIN_MENU_LABEL = "Main Menu";
    public static final String JMENU_ITEM_NEW_GAME_LABEL = "New Game";
    public static final int MARGIN_LARGE = 350;
    public static final int MARGIN_MEDIUM = 100;
    public static final int MARGIN_SMALL = 25;
    public static final int MARGIN_EXTRA_SMALL = 10;
    public static final Preferences PREFERENCES_NODE = Preferences.userRoot().node(UiConstants.SWING_APPLICATION_PACKAGE_NAME);
    public static final int SLEEP_INTERVAL = 1000;

    private UiConstants() {}

    public static String getInputErrorMessage(String input)
    {
        return String.format("Your input of \"%s\" is invalid.", input);
    }

    public static Dimension getGameConfigJDialogDimension()
    {
        return new Dimension(
            (int) (BLACKJACK_JFRAME_DIMENSION.width * GAME_CONFIG_JDIALOG_WIDTH_FACTOR),
            (int) (BLACKJACK_JFRAME_DIMENSION.height * GAME_CONFIG_JDIALOG_HEIGHT_FACTOR)
        );
    }
}