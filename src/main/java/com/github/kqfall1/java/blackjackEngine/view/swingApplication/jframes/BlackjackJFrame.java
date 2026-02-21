package com.github.kqfall1.java.blackjackEngine.view.swingApplication.jframes;

import com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents.ApplicationBackground;
import com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents.ApplicationJMenuBar;
import static com.github.kqfall1.java.blackjackEngine.view.swingApplication.UiConstants.*;
import com.github.kqfall1.java.blackjackEngine.view.swingApplication.UiActions;
import java.awt.*;
import javax.swing.*;

/**
 * Provides abstraction for basic configuration for the {@code JFrame} objects used for this blackjack app.
 *
 * @author kqfall1
 * @since 08/02/2026
 */
public abstract class BlackjackJFrame extends JFrame
{
    public BlackjackJFrame()
    {
        final var ICON_IMAGE_HEIGHT = 32;
        final var ICON_IMAGE_WIDTH = 32;
        final var UI_ACTIONS = UiActions.getInstance();

        final var BACKGROUND = new ApplicationBackground();
        BACKGROUND.setLayout(new BorderLayout());

        setContentPane(BACKGROUND);
        setIconImage(
            BLACKJACK_JFRAME_LOGO.getImage().getScaledInstance(
                ICON_IMAGE_WIDTH,
                ICON_IMAGE_HEIGHT,
                Image.SCALE_SMOOTH
            )
        );
        setJMenuBar(new ApplicationJMenuBar(UI_ACTIONS));
        UI_ACTIONS.setKeystrokes(this);
        setResizable(false);
        setSize(BLACKJACK_JFRAME_DIMENSION);
        setTitle(BLACKJACK_JFRAME_TITLE);
    }
}