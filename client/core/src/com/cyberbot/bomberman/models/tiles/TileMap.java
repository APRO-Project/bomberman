package com.cyberbot.bomberman.models.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.cyberbot.bomberman.models.Drawable;

import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.List;

public class TileMap implements Disposable, Drawable {
    private static final String LAYER_BASE = "base";
    private static final String LAYER_FLOOR = "floor";
    private static final String LAYER_WALLS = "walls";

    private final List<TileMapLayer> othersLayers;

    private TileMapLayer baseLayer;
    private TileMapLayer floorLayer;
    private TileMapLayer wallsLayer;

    public TileMap(World world, String path) throws InvalidPropertiesFormatException {
        TiledMap sourceMap = new TmxMapLoader().load(path);

        othersLayers = new ArrayList<>();

        for (MapLayer layer : sourceMap.getLayers()) {
            if (layer instanceof TiledMapTileLayer) {
                TileMapLayer mapLayer = new TileMapLayer((TiledMapTileLayer) layer, world);
                switch (layer.getName()) {
                    case LAYER_BASE:
                        baseLayer = mapLayer;
                        break;
                    case LAYER_FLOOR:
                        floorLayer = mapLayer;
                        break;
                    case LAYER_WALLS:
                        wallsLayer = mapLayer;
                        break;
                    default:
                        othersLayers.add(mapLayer);
                }

            }
        }

        sourceMap.dispose();
    }

    public TileMapLayer getFloor() {
        return floorLayer;
    }

    public TileMapLayer getWalls() {
        return wallsLayer;
    }

    @Override
    public void draw(SpriteBatch batch) {
        baseLayer.draw(batch);
        floorLayer.draw(batch);
        wallsLayer.draw(batch);
        othersLayers.forEach(t -> t.draw(batch));
    }

    @Override
    public void dispose() {
        baseLayer.dispose();
        floorLayer.dispose();
        wallsLayer.dispose();
        othersLayers.forEach(TileMapLayer::dispose);
    }
}
