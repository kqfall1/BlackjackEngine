package com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents;

import com.github.kqfall1.java.blackjackEngine.view.swingApplication.UiConstants;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javax.swing.*;

/**
 * Contains {@code JComponent} objects required to credit the sources of media used in this game.
 *
 * @author kqfall1
 * @since 16/04/2026
 */
public final class CreditsJPanel extends JPanel
{
    private final JTextArea sourcesJTextArea;

    public CreditsJPanel()
    {
        String sources;

        try (InputStream in = CreditsJPanel.class.getResourceAsStream("/sources.md"))
        {
            sources = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
        catch (IOException e)
        {
            sources = UiConstants.GAME_MESSAGE_NO_SOURCES_FOUND;
        }

        sourcesJTextArea = new JTextArea(sources);
        sourcesJTextArea.setLineWrap(true);
        sourcesJTextArea.setMargin(UiConstants.JTEXT_AREA_INSETS);
        sourcesJTextArea.setEditable(false);
        final var sourcesJScrollPane = new JScrollPane(sourcesJTextArea);
        sourcesJScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        setLayout(new BorderLayout());
        add(sourcesJScrollPane, BorderLayout.CENTER);
    }
}