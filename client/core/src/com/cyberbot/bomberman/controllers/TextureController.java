package com.cyberbot.bomberman.controllers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.cyberbot.bomberman.models.Drawable;
import com.cyberbot.bomberman.models.Updatable;
import com.cyberbot.bomberman.models.entities.BombEntity;
import com.cyberbot.bomberman.models.entities.Entity;
import com.cyberbot.bomberman.models.entities.PlayerEntity;
import com.cyberbot.bomberman.models.tiles.Tile;
import com.cyberbot.bomberman.models.tiles.TileMap;
import com.cyberbot.bomberman.sprites.BombSprite;
import com.cyberbot.bomberman.sprites.EntitySprite;
import com.cyberbot.bomberman.sprites.PlayerSprite;
import com.cyberbot.bomberman.sprites.TileSprite;

import java.util.ArrayList;
import java.util.List;

public class TextureController implements Drawable, Updatable, GameStateController.ChangeListener, TileMap.ChangeListener {
    private final List<BombSprite> bombs;
    private final List<PlayerSprite> players;
    private final List<TileSprite> base;
    private final List<TileSprite> floor;
    private final List<TileSprite> walls;

    public TextureController(TileMap map) {
        map.addListener(this);
        this.bombs = new ArrayList<>();
        this.players = new ArrayList<>();
        this.base = TileSprite.fromTileLayer(map.getBase());
        this.floor = TileSprite.fromTileLayer(map.getFloor());
        this.walls = TileSprite.fromTileLayer(map.getWalls());
    }

    @Override
    public void draw(SpriteBatch batch) {
        base.forEach(sprite -> sprite.draw(batch));
        floor.forEach(sprite -> sprite.draw(batch));
        walls.forEach(sprite -> sprite.draw(batch));
        bombs.forEach(sprite -> sprite.draw(batch));
        players.forEach(sprite -> sprite.draw(batch));
    }

    @Override
    public void update(float delta) {
        bombs.forEach(sprite -> sprite.update(delta));
        players.forEach(sprite -> sprite.update(delta));
    }

    @Override
    public void onBombAdded(BombEntity bomb) {
        bombs.add(new BombSprite(bomb));
    }

    @Override
    public void onBombRemoved(BombEntity bomb) {
        removeEntity(bombs, bomb);
    }

    @Override
    public void onPlayerAdded(PlayerEntity player) {
        players.add(new PlayerSprite(player));
    }

    @Override
    public void onPlayerRemoved(PlayerEntity player) {
        removeEntity(players, player);
    }

    @Override
    public void onWallAdded(Tile tile) {
        walls.add(new TileSprite(tile));
    }

    @Override
    public void onWallRemoved(Tile tile) {
        TileSprite sprite = walls.stream()
                .filter(p -> p.getTile().equals(tile))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("WallTile not present in TextureController"));
        sprite.dispose();
        walls.remove(sprite);
    }

    private <E extends Entity> void removeEntity(List<? extends EntitySprite<E>> list, E entity) {
        EntitySprite<E> sprite = list.stream()
                .filter(p -> p.getEntity().equals(entity))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Entity not present in TextureController"));
        sprite.dispose();
        list.remove(sprite);
    }
}
