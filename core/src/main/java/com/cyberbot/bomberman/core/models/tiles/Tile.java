package com.cyberbot.bomberman.core.models.tiles;

import com.badlogic.gdx.math.Vector2;
import com.cyberbot.bomberman.core.utils.Constants;

import static com.cyberbot.bomberman.core.utils.Constants.PPM;

/**
 * Base class for all map tiles. Includes texture and position information.
 * The position of the tiles index from (0, 0) and follow the euclidean coordinate system.
 */
public class Tile {

    private final String textureName;
    private final int x;
    private final int y;

    public Tile(String textureName, int x, int y) {
        this.textureName = textureName;
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getTextureName() {
        return textureName;
    }

    /**
     * Returns the position in the pixel coordinate system
     *
     * @return the position in the pixel coordinate system
     * @see Constants#PPM
     */
    public Vector2 getPosition() {
        return new Vector2((x + 0.5f) * PPM, (y + 0.5f) * PPM);
    }
}
