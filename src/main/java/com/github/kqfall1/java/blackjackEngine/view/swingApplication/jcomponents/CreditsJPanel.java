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
        var htmlBuilder = new StringBuilder("<html><ul>");

        try (InputStream in = CreditsJPanel.class.getResourceAsStream("/sources.md"))
        {
            final var scanner = new Scanner(in);
            while (scanner.hasNextLine())
            {
                htmlBuilder.append(String.format("<li>%s</li>", scanner.nextLine().trim()));
            }
            htmlBuilder.append("</ul></html>");
        }
        catch (IOException e)
        {
            htmlBuilder = new StringBuilder("<html><p>No sources found!</p></html>");
        }

        sourcesJLabel = new JLabel(htmlBuilder.toString());
        add(sourcesJLabel);
    }
}