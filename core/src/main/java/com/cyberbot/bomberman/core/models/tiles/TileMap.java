package com.cyberbot.bomberman.core.models.tiles;

import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;
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

    private final TileMapLayer baseLayer;
    private final TileMapLayer floorLayer;
    private final TileMapLayer wallsLayer;

    private final List<ChangeListener> listeners;

    public TileMap(TileMapLayer baseLayer, TileMapLayer floorLayer, TileMapLayer wallsLayer)
        throws MissingLayersException {
        listeners = new ArrayList<>();
        this.baseLayer = baseLayer;
        this.floorLayer = floorLayer;
        this.wallsLayer = wallsLayer;

        if (baseLayer == null) {
            throw new MissingLayersException("Base layer missing");
        }
        if (floorLayer == null) {
            throw new MissingLayersException("Floor layer missing");
        }
        if (wallsLayer == null) {
            throw new MissingLayersException("Walls layer missing");
        }
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

    public void addWall(Tile tile) {
        wallsLayer.add(tile);
        listeners.forEach(listener -> listener.onWallAdded(tile));
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
         * @param tile The removed tile.
         */
        void onWallRemoved(Tile tile);
    }
}
