package com.cyberbot.bomberman.controllers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.cyberbot.bomberman.models.Drawable;
import com.cyberbot.bomberman.models.entities.Entity;
import com.cyberbot.bomberman.models.entities.PlayerEntity;
import com.cyberbot.bomberman.models.tiles.BaseTile;
import com.cyberbot.bomberman.models.tiles.FloorTile;
import com.cyberbot.bomberman.models.tiles.TileMap;

import java.util.ArrayList;
import java.util.List;

public class GameStateController implements Drawable, Disposable {
    private final TileMap map;
    private final List<Entity> entities;
    private final List<PlayerEntity> players;

    public GameStateController(TileMap map, List<PlayerEntity> players) {
        this.map = map;
        this.players = players;
        entities = new ArrayList<>();
    }

    public void update(float delta) {
        players.forEach(player -> {
            player.update(delta);
            Vector2 position = player.getPositionRaw();
            int x = (int) Math.floor(position.x);
            int y = (int) Math.floor(position.y);

            BaseTile tile = map.getFloor().getTile(x, y);
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
    public void draw(SpriteBatch batch) {
        map.draw(batch);
        entities.forEach(entity -> entity.draw(batch));
        players.forEach(player -> player.draw(batch));
    }

    @Override
    public void dispose() {
        map.dispose();
        entities.forEach(Entity::dispose);
        players.forEach(PlayerEntity::dispose);
    }
}
