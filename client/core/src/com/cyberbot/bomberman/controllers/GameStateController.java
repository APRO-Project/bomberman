package com.cyberbot.bomberman.controllers;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.cyberbot.bomberman.models.Updatable;
import com.cyberbot.bomberman.models.defs.BombDef;
import com.cyberbot.bomberman.models.entities.BombEntity;
import com.cyberbot.bomberman.models.entities.Entity;
import com.cyberbot.bomberman.models.entities.PlayerEntity;
import com.cyberbot.bomberman.models.tiles.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GameStateController implements Disposable, Updatable, ActionController.Listener {
    private final World world;

    private final TileMap map;
    private final List<BombEntity> bombs;
    private final List<PlayerEntity> players;
    private ChangeListener listener;

    public GameStateController(World world, TileMap map) {
        this.world = world;
        this.map = map;
        this.players = new ArrayList<>();
        this.bombs = new ArrayList<>();
    }

    @Override
    public void update(float delta) {
        players.forEach(player -> {
            player.update(delta);
            Vector2 position = player.getPositionRaw();
            int x = (int) Math.floor(position.x);
            int y = (int) Math.floor(position.y);

            Tile tile = map.getFloor().getTile(x, y);
            if(tile instanceof FloorTile) {
                FloorTile.Properties properties = ((FloorTile) tile).getProperties();
                player.setDragModifier(properties.dragMultiplier);
                player.setMaxSpeedModifier(properties.maxSpeedMultiplier);
            } else {
                player.setDragModifier(1);
                player.setMaxSpeedModifier(1);
            }
        });

        bombs.forEach(bomb -> {
            bomb.update(delta);
            if(bomb.isBlown()) {
                onBombExploded(bomb);
            }
        });

        bombs.removeIf(BombEntity::isBlown);
    }

    @Override
    public void dispose() {
        map.dispose();
        bombs.forEach(Entity::dispose);
        players.forEach(PlayerEntity::dispose);
    }

    public void addPlayers(Collection<PlayerEntity> players) {
        this.players.addAll(players);
        if(listener != null) {
            players.forEach(player -> listener.onPlayerAdded(player));
        }
    }

    public Iterable<PlayerEntity> getPlayers() {
        return players;
    }

    public void setListener(ChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBombPlaced(BombDef bombDef, PlayerEntity executor) {
        BombEntity bomb = new BombEntity(world, bombDef);
        Vector2 position = executor.getPositionRaw();
        float x = (float) Math.floor(position.x) + 0.5F;
        float y = (float) Math.floor(position.y) + 0.5F;

        bomb.setPositionRaw(new Vector2(x, y));

        bombs.add(bomb);
        listener.onBombAdded(bomb);

    }

    private void onBombExploded(BombEntity bomb) {
        listener.onBombRemoved(bomb);
        bomb.dispose();

        int range = (int) bomb.getRange();
        Vector2 position = bomb.getPositionRaw();
        int x = (int) position.x;
        int y = (int) position.y;

        float powerRight = bomb.getPower();
        float powerLeft = bomb.getPower();
        float powerUp = bomb.getPower();
        float powerDown = bomb.getPower();

        TileMapLayer walls = map.getWalls();

        // Right
        for (int i = 1; i <= range; i++) {
            Tile tile = walls.getTile(x + i, y);
            powerRight = damageTile(tile, powerRight);
            if(powerRight == 0) {
                break;
            }
        }

        // Left
        for (int i = 1; i <= range; i++) {
            Tile tile = walls.getTile(x - i, y);
            powerLeft = damageTile(tile, powerLeft);
            if(powerLeft == 0) {
                break;
            }
        }

        // Up
        for (int i = 1; i <= range; i++) {
            Tile tile = walls.getTile(x, y + i);
            powerUp = damageTile(tile, powerUp);
            if(powerUp == 0) {
                break;
            }
        }

        // Down
        for (int i = 1; i <= range; i++) {
            Tile tile = walls.getTile(x, y - i);
            powerDown = damageTile(tile, powerDown);
            if(powerDown == 0) {
                break;
            }
        }
    }

    private float damageTile(Tile tile, float power) {
        if(tile instanceof WallTile) {
            WallTile.Properties props = ((WallTile) tile).getProperties();
            float durability = props.durability;
            if(durability == WallTile.Properties.DURABILITY_INFINITE) {
                return 0;
            }

            if(power >= durability) {
                map.removeWall(tile.getX(), tile.getY());
                return power - durability;
            } else {
                // TODO: Maybe implement wall durability decrease
            }
        }

        return power - WallTile.Properties.POWER_DROPOFF;
    }

    public interface ChangeListener {
        void onBombAdded(BombEntity bomb);

        void onBombRemoved(BombEntity bomb);

        void onPlayerAdded(PlayerEntity player);

        void onPlayerRemoved(PlayerEntity player);
    }
}
