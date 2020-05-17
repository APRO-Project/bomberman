package com.cyberbot.bomberman.core.models.tiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.List;

/**
 * Represents the playable map.
 * Contains 3 layers:
 * <ul>
 *     <li>Base - the base floor layer, should only contain the textures</li>
 *     <li>Floor - the floor layer, contains {@link FloorTile FloorTiles} with some extra properties</li>
 *     <li>Walls - the wall layer, contains {@link WallTile WallTiles} with some extra properties</li>
 * </ul>
 */
public class TileMap implements Disposable {
    private static final String TAG = TileMap.class.getSimpleName();

    private static final String LAYER_BASE = "base";
    private static final String LAYER_FLOOR = "floor";
    private static final String LAYER_WALLS = "walls";

    private TileMapLayer baseLayer = null;
    private TileMapLayer floorLayer = null;
    private TileMapLayer wallsLayer = null;

    private final List<ChangeListener> listeners;

    /**
     * Creates a new instance of the TileMap.
     *
     * @param world The Box2D world to bind {@link PhysicalTile PhysicalTiles} to.
     * @param path  A path to the TMX file to load the map from.
     * @throws InvalidPropertiesFormatException When some required properties where missing
     *                                          or were of an invalid type for any of the tiles.
     * @throws IllegalArgumentException         When a tile property contains an illegal value.
     * @throws MissingLayersException           When any of the required layers where missing.
     * @see TileMapLayer#TileMapLayer(TiledMapTileLayer, World)
     * @see TileFactory#createTile(TiledMapTile, World, int, int)
     */
    public TileMap(World world, String path) throws InvalidPropertiesFormatException, MissingLayersException {
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

        if (baseLayer == null) {
            throw new MissingLayersException("Base layer missing");
        }
        if (floorLayer == null) {
            throw new MissingLayersException("Floor layer missing");
        }
        if (wallsLayer == null) {
            throw new MissingLayersException("Walls layer missing");
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

    /**
     * An interface that any parties interested in changes to this map should implement.
     */
    public interface ChangeListener {
        /**
         * Called when a new Tile has been added to the wall layer.
         *
         * @param tile The new tile.
         */
        void onWallAdded(Tile tile);

        /**
         * Called when a new Tile has been removed from the wall layer.
         *
         * @param tile The new tile.
         */
        void onWallRemoved(Tile tile);
    }
}
