package com.github.kqfall1.java.blackjackEngine.model.interfaces;

import com.github.kqfall1.java.blackjackEngine.model.cards.Card;

/**
 * Defines the methods that all {@code Card} containers must implement to
 * remove a {@code Card} and play it in a game.
 *
 * @author kqfall1
 * @since 31/12/2025
 */
public interface Drawable
{
	Card draw();
}