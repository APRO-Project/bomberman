package com.cyberbot.bomberman.core.models.tiles;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TileMapLayer implements Disposable, Collection<Tile> {
    private final int width;
    private final int height;
    private final Tile[][] tiles;

    /**
     * Creates a new layer from a LibGDX {@link TiledMapTileLayer}.
     *
     * @param mapTileLayer Layer source.
     * @param world        Box2D world for binding {@link PhysicalTile PhysicalTiles}.
     * @throws InvalidPropertiesFormatException When some required properties where missing
     *                                          or were of an invalid type for any of the tiles.
     * @throws IllegalArgumentException         When a tile property contains an illegal value.
     * @see TileFactory#createTile(TiledMapTile, World, int, int)
     */
    public TileMapLayer(TiledMapTileLayer mapTileLayer, World world)
        throws InvalidPropertiesFormatException {

        width = mapTileLayer.getWidth();
        height = mapTileLayer.getHeight();

        tiles = new Tile[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                TiledMapTileLayer.Cell cell = mapTileLayer.getCell(x, y);

                if (cell == null) {
                    continue;
                }

                tiles[x][y] = TileFactory.createTile(cell.getTile(), world, x, y);
            }
        }
    }

    public Tile getTile(int x, int y) {
        return tiles[x][y];
    }

    Tile removeTile(int x, int y) {
        Tile tile = getTile(x, y);
        if (tile != null) {
            tiles[x][y] = null;
            if (tile instanceof PhysicalTile) {
                ((PhysicalTile) tile).dispose();
            }
        }

        return tile;
    }

    @Override
    public void dispose() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (tiles[x][y] instanceof Disposable) {
                    ((Disposable) tiles[x][y]).dispose();
                }
            }
        }
    }

    @Override
    public int size() {
        return getFlatList().size();
    }

    @Override
    public boolean isEmpty() {
        return getFlatList().isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return getFlatList().contains(o);
    }

    @Override
    public Iterator<Tile> iterator() {
        return getFlatList().iterator();
    }

    @Override
    public Object[] toArray() {
        return getFlatList().toArray();
    }

    @Override
    @Deprecated
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override
    public boolean add(Tile tile) {
        Tile previous = tiles[tile.getX()][tile.getY()];
        tiles[tile.getX()][tile.getY()] = tile;

        return Objects.equals(previous, tile);
    }

    @Override
    public boolean remove(Object o) {
        if (o instanceof Tile) {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    Tile tile = tiles[x][y];
                    if (o.equals(tile)) {
                        if (tile instanceof Disposable) {
                            ((Disposable) tile).dispose();
                        }
                        tiles[x][y] = null;

                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return getFlatList().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Tile> c) {
        boolean changed = false;
        for (Tile t : c) {
            changed = add(t) || changed;
        }
        return changed;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean changed = false;
        for (Object o : c) {
            changed = remove(o) || changed;
        }
        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean changed = false;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Tile tile = tiles[x][y];
                if (!c.contains(tile)) {
                    if (tile instanceof Disposable) {
                        ((Disposable) tile).dispose();
                    }
                    tiles[x][y] = null;
                    changed = true;
                }
            }
        }

        return changed;
    }

    @Override
    public void clear() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Tile tile = tiles[x][y];
                if (tile instanceof Disposable) {
                    ((Disposable) tile).dispose();
                }
                tiles[x][y] = null;
            }
        }
    }

    @Override
    public void forEach(Consumer<? super Tile> action) {
        getFlatList().forEach(action);
    }

    private List<Tile> getFlatList() {
        return Arrays.stream(tiles)
            .map(Arrays::asList)
            .flatMap(List::stream)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
}
