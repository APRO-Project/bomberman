package com.cyberbot.bomberman.models.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.cyberbot.bomberman.models.Drawable;

import java.util.InvalidPropertiesFormatException;

public class TileMapLayer implements Drawable, Disposable {
    private int width;
    private int height;
    private BaseTile[][] tiles;

    public TileMapLayer(TiledMapTileLayer mapTileLayer, World world)
            throws InvalidPropertiesFormatException {

        width = mapTileLayer.getWidth();
        height = mapTileLayer.getHeight();

        tiles = new BaseTile[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                TiledMapTileLayer.Cell cell = mapTileLayer.getCell(x, y);

                if (cell == null) {
                    continue;
                }

                tiles[x][y] = BaseTile.fromMapTile(cell.getTile(), world, new Vector2(x, y));
            }
        }
    }

    public BaseTile getTile(int x, int y) {
        return tiles[x][y];
    }

    @Override
    public void draw(SpriteBatch batch) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (tiles[x][y] != null) tiles[x][y].draw(batch);
            }
        }
    }

    @Override
    public void dispose() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (tiles[x][y] != null) tiles[x][y].dispose();
            }
        }
    }
}
