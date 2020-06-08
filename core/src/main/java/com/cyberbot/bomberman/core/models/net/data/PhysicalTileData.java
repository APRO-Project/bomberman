package com.cyberbot.bomberman.core.models.net.data;

import com.badlogic.gdx.physics.box2d.World;
import com.cyberbot.bomberman.core.models.tiles.PhysicalTile;

import java.io.Serializable;

public abstract class PhysicalTileData<E extends PhysicalTile> implements Serializable {
    protected final int x;
    protected final int y;
    protected final String textureName;

    public PhysicalTileData(int x, int y, String textureName) {
        this.x = x;
        this.y = y;
        this.textureName = textureName;
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

    public abstract E createTile(World world);
}
