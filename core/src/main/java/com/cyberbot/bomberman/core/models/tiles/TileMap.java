package com.cyberbot.bomberman.core.models.tiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.List;

public class TileMap implements Disposable {
    private static final String TAG = TileMap.class.getSimpleName();

    private static final String LAYER_BASE = "base";
    private static final String LAYER_FLOOR = "floor";
    private static final String LAYER_WALLS = "walls";

    private TileMapLayer baseLayer;
    private TileMapLayer floorLayer;
    private TileMapLayer wallsLayer;

    private List<ChangeListener> listeners;

    public TileMap(World world, String path) throws InvalidPropertiesFormatException {
        TiledMap sourceMap = new TmxMapLoader().load(path);

        listeners = new ArrayList<>();

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
                        Gdx.app.error(TAG, "Unsupported tile layer found: '" +
                            layer.getName() + "', will be ignored");
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

    public TileMapLayer getBase() {
        return baseLayer;
    }

    public void addListener(ChangeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ChangeListener listener) {
        listeners.remove(listener);
    }

    public void clearListeners() {
        listeners.clear();
    }

    public void removeWall(int x, int y) {
        Tile tile = wallsLayer.removeTile(x, y);
        listeners.forEach(listener -> listener.onWallRemoved(tile));
    }

    @Override
    public void dispose() {
        baseLayer.dispose();
        floorLayer.dispose();
        wallsLayer.dispose();
    }

    public interface ChangeListener {
        void onWallAdded(Tile tile);

        void onWallRemoved(Tile tile);
    }
}
