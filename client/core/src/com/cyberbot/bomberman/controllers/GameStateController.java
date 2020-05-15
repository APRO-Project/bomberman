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
import com.cyberbot.bomberman.models.tiles.Tile;
import com.cyberbot.bomberman.models.tiles.FloorTile;
import com.cyberbot.bomberman.models.tiles.TileMap;

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
        BombEntity entity = new BombEntity(world, bombDef);
        Vector2 position = executor.getPositionRaw();
        float x = (float) Math.floor(position.x) + 0.5F;
        float y = (float) Math.floor(position.y) + 0.5F;

        entity.setPositionRaw(new Vector2(x, y));

        listener.onBombAdded(entity);

    }

    public interface ChangeListener {
        void onBombAdded(BombEntity bomb);

        void onBombRemoved(BombEntity bomb);

        void onPlayerAdded(PlayerEntity player);

        void onPlayerRemoved(PlayerEntity player);
    }
}
