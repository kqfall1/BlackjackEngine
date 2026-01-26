package com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents;

import com.github.kqfall1.java.blackjackEngine.view.swingApplication.UiConstants;
import java.awt.*;
import javax.swing.*;

/**
 * Provides a background to be used by all {@code JFrame} objects for the Swing user interface.
 *
 * @author kqfall1
 * @since 24/01/2026
 */
public final class ApplicationBackground extends JPanel
{
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(
            UiConstants.MAIN_MENU_BACKGROUND.getImage(),
            0,
            0,
            null
        );
    }
}