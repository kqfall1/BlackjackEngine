package com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents;
import com.github.kqfall1.java.interfaces.ErrorPresenter;

import javax.swing.*;

/**
 * Contains all {@code JComponent} objects required by a {@code MainMenuJFrame} to configure a new blackjack game.
 *
 * <p>Encapsulates an {@code InputManager} that accepts input from {@code NumberJTextField} and
 * {@code StringJTextField} objects. Implements {@code ErrorPresenter} to improve the application's user experience.</p>
 *
 * @author kqfall1
 * @since 14/02/2026
 */
public final class GameConfigJPanel extends JPanel implements ErrorPresenter
{


    public GameConfigJPanel()
    {

    }

    @Override
    public void showError(String message)
    {

    }

    @Override
    public void showException(Exception e)
    {

    }
}