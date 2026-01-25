package com.github.kqfall1.java.blackjackEngine.view.swingApplication;

import java.awt.*;
import javax.swing.*;

/**
 * A static class that holds constant values and data structures required for the Swing blackjack app.
 *
 * @author kqfall1
 * @since 24/01/2026
 */
public final class SwingApplicationConstants
{
    public static final Dimension JFRAME_DIMENSION = Toolkit.getDefaultToolkit().getScreenSize();
    public static final String JFRAME_TITLE = "Blackjack";
    public static final ImageIcon MAIN_MENU_BACKGROUND
            = new ImageIcon(SwingApplicationConstants.class.getResource("/images/MainMenuBackground.jpg"));

    private SwingApplicationConstants() {}
}