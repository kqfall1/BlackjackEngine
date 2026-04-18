package com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents;

import com.github.kqfall1.java.blackjackEngine.model.cards.Card;
import com.github.kqfall1.java.blackjackEngine.view.swingApplication.ui.UiConstants;
import com.github.kqfall1.java.utils.StringUtils;
import javax.swing.*;
import java.awt.*;

/**
 * A {@code JLabel} to hold an {@code ImageIcon} to render cards.
 *
 * @author kqfall1
 * @since 15/03/2026
 */
public final class CardJLabel extends JLabel
{
    private final Card card;
    private boolean faceUp;
    private static final String RESOURCE_LOOKUP_STRING_PREFIX = "/images/cards/";

    public CardJLabel(Card card, boolean faceUp)
    {
        this.card = card;
        setFaceUp(faceUp);
        setOpaque(false);
    }

    public void setFaceUp(boolean faceUp)
    {
        this.faceUp = faceUp;
        updateImageIcon();
    }

    public void updateImageIcon()
    {
        String resourceLookupString;

        if (faceUp)
        {
            resourceLookupString = String.format(
                "%s%s_of_%ss.png",
                RESOURCE_LOOKUP_STRING_PREFIX,
                StringUtils.normalizeLower(card.getRank().toString()),
                StringUtils.normalizeLower(card.getSuit().toString())
            );
        }
        else
        {
            resourceLookupString = String.format("%s%s", RESOURCE_LOOKUP_STRING_PREFIX, UiConstants.BACK_OF_CARD_IMAGE_FILE_NAME);
        }

        final var cardImage = new ImageIcon(CardJLabel.class.getResource(resourceLookupString)).getImage().getScaledInstance(
            UiConstants.CARD_IMAGE_WIDTH,
            UiConstants.CARD_IMAGE_HEIGHT,
            Image.SCALE_SMOOTH
        );
        setIcon(new ImageIcon(cardImage));
    }
}