package com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Scanner;
import javax.swing.*;

/**
 * Contains {@code JComponent} objects required to credit the sources of media used in this game.
 *
 * @author kqfall1
 * @since 16/04/2026
 */
public final class CreditsJPanel extends JPanel
{
    private final JLabel sourcesJLabel;

    public CreditsJPanel()
    {
        var sourcesString = "<ul>";

        try (InputStream in = CreditsJPanel.class.getResourceAsStream("/sources.md"))
        {
            final var scanner = new Scanner(in);

            while (scanner.hasNextLine())
            {
                var sourceStringFormat = "%s<li>%s</li>";

                if (!scanner.hasNextLine())
                {
                    sourceStringFormat = String.format("%s</ul>", sourceStringFormat);
                }

                sourcesString = String.format(sourceStringFormat, sourcesString, scanner.nextLine().trim());
            }
        }
        catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }

        sourcesJLabel = new JLabel(sourcesString);
        add(sourcesJLabel, BorderLayout.CENTER);
    }
}