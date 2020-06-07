package com.cyberbot.bomberman.core.models.net.snapshots;

import com.cyberbot.bomberman.core.models.entities.Entity;
import com.cyberbot.bomberman.core.models.net.data.EntityData;
import com.cyberbot.bomberman.core.models.net.data.WallTileData;
import com.cyberbot.bomberman.core.models.tiles.Tile;
import com.cyberbot.bomberman.core.models.tiles.WallTile;

import java.io.Serializable;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameSnapshot implements Serializable {
    private final Map<Long, EntityData<?>> entities;
    private final Map<Integer, WallTileData> walls;

    public GameSnapshot(Stream<Entity> entities, Stream<Tile> walls) {
        this.entities = entities.collect(Collectors.toMap(Entity::getId, Entity::getData));
        this.walls = walls
            .filter(it -> it instanceof WallTile)
            .collect(Collectors.toMap(Tile::hashCode, it -> ((WallTile) it).getData()));
    }

    public boolean hasEntity(long id) {
        return entities.containsKey(id);
    }

    public boolean hasWall(int hash) {
        return walls.containsKey(hash);
    }

    public EntityData<?> getEntity(long id) {
        return entities.get(id);
    }

    public Map<Long, EntityData<?>> getEntities() {
        return entities;
    }

    public Map<Integer, WallTileData> getWalls() {
        return walls;
    }
}
