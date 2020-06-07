package com.cyberbot.bomberman.core.models.net.data;

import com.badlogic.gdx.physics.box2d.World;
import com.cyberbot.bomberman.core.models.tiles.WallTile;

public class WallTileData extends PhysicalTileData<WallTile> {
    private final WallTile.Properties properties;

    public WallTileData(int x, int y, String textureName, WallTile.Properties properties) {
        super(x, y, textureName);
        this.properties = properties;
    }

    @Override
    public WallTile createTile(World world) {
        return new WallTile(world, textureName, properties, x, y);
    }
}
