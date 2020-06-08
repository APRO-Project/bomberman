package com.cyberbot.bomberman.models;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Basic interface that any drawable object should implement
 */
public interface Drawable {
    /**
     * Called when the object should draw itself.
     * No updating should be done during a draw,
     * all heavy updates should be done sparely,
     * ex. by implementing {@link com.cyberbot.bomberman.core.models.Updatable Updatable}
     *
     * @param batch The sprite batch used for drawing
     */
    void draw(SpriteBatch batch);
}
