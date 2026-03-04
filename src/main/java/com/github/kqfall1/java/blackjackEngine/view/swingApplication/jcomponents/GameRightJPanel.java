package com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents;

import javax.swing.*;

/**
 * Contains all {@code JComponent} objects required to perform core blackjack actions.
 *
 * @author kqfall1
 * @since 03/03/2026
 */
public class GameRightJPanel extends JPanel
{
    private final JButton doubleDownJButton;
    private final JButton hitJButton;
    private final JButton splitJButton;
    private final JButton standJButton;
    private final JButton surrenderJButton;

    public GameRightJPanel()
    {
        doubleDownJButton = new JButton();
    }
}