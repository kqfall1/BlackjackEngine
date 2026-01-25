package com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents;

import com.github.kqfall1.java.blackjackEngine.view.swingApplication.SwingApplicationConstants;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ApplicationJMenuBar extends JMenuBar
{
    public ApplicationJMenuBar()
    {
        final var FILE_MENU = new JMenu(SwingApplicationConstants.JMENU_FILE_LABEL);

        final var EXIT_ACTION = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                System.exit(0);
            }
        };
        EXIT_ACTION.putValue(Action.NAME, SwingApplicationConstants.JMENU_ITEM_EXIT_LABEL);
        EXIT_ACTION.putValue(Action.SHORT_DESCRIPTION, SwingApplicationConstants.JMENU_ITEM_EXIT_LABEL);
        FILE_MENU.add(EXIT_ACTION);

        final var NEW_GAME_ACTION = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {

            }
        };
        NEW_GAME_ACTION.putValue(Action.NAME, SwingApplicationConstants.JMENU_ITEM_NEW_GAME_LABEL);
        NEW_GAME_ACTION.putValue(Action.SHORT_DESCRIPTION, SwingApplicationConstants.JMENU_ITEM_NEW_GAME_LABEL);
        FILE_MENU.add(NEW_GAME_ACTION);

        add(FILE_MENU);
    }
}