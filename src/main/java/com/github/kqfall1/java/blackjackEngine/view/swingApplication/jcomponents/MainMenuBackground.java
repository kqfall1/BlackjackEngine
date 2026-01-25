package com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents;

import com.github.kqfall1.java.blackjackEngine.view.swingApplication.SwingApplicationConstants;
import java.awt.*;
import javax.swing.*;

public final class MainMenuBackground extends JPanel
{
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(
                SwingApplicationConstants.MAIN_MENU_BACKGROUND.getImage(),
                0,
                0,
                null
        );
    }
}