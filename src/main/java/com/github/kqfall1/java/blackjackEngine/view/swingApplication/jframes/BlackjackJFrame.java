package com.github.kqfall1.java.blackjackEngine.view.swingApplication.jframes;

import com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents.ApplicationBackground;
import com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents.ApplicationJMenuBar;
import static com.github.kqfall1.java.blackjackEngine.view.swingApplication.ui.UiConstants.*;
import com.github.kqfall1.java.blackjackEngine.view.swingApplication.ui.UiActions;
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
        final var iconImageHeight = 32;
        final var iconImageWidth = 32;
        final var uiActions = UiActions.getInstance();
        final var background = new ApplicationBackground();
        background.setLayout(new BorderLayout());
        setContentPane(background);
        setIconImage(BLACKJACK_JFRAME_LOGO.getImage().getScaledInstance(iconImageWidth, iconImageHeight, Image.SCALE_SMOOTH));
        setJMenuBar(new ApplicationJMenuBar(uiActions));
        uiActions.setKeystrokes(this);
        setResizable(false);
        setSize(BLACKJACK_JFRAME_DIMENSION);
        setTitle(BLACKJACK_JFRAME_TITLE);
    }
}