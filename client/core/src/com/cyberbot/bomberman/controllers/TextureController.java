package com.cyberbot.bomberman.controllers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.cyberbot.bomberman.models.Drawable;
import com.cyberbot.bomberman.models.Updatable;
import com.cyberbot.bomberman.models.entities.Entity;
import com.cyberbot.bomberman.models.factories.SpriteFactory;
import com.cyberbot.bomberman.models.tiles.Tile;
import com.cyberbot.bomberman.models.tiles.TileMap;
import com.cyberbot.bomberman.sprites.EntitySprite;
import com.cyberbot.bomberman.sprites.TileSprite;

import java.util.ArrayList;
import java.util.List;

public class TextureController implements Drawable, Updatable, GameStateController.ChangeListener, TileMap.ChangeListener {
    private final List<EntitySprite<?>> entities;
    private final List<TileSprite> base;
    private final List<TileSprite> floor;
    private final List<TileSprite> walls;

    public TextureController(TileMap map) {
        map.addListener(this);
        this.entities = new ArrayList<>();
        this.base = TileSprite.fromTileLayer(map.getBase());
        this.floor = TileSprite.fromTileLayer(map.getFloor());
        this.walls = TileSprite.fromTileLayer(map.getWalls());
    }

    @Override
    public void draw(SpriteBatch batch) {
        base.forEach(sprite -> sprite.draw(batch));
        floor.forEach(sprite -> sprite.draw(batch));
        walls.forEach(sprite -> sprite.draw(batch));
        entities.forEach(sprite -> sprite.draw(batch));
    }

    @Override
    public void update(float delta) {
        entities.forEach(sprite -> sprite.update(delta));
    }


    @Override
    public void onWallAdded(Tile tile) {
        walls.add(new TileSprite(tile));
    }

    @Override
    public void onWallRemoved(Tile tile) {
        walls.removeIf(sprite -> sprite.getTile().equals(tile));
    }

    @Override
    public void onEntityAdded(Entity entity) {
        entities.add(0, SpriteFactory.createEntitySprite(entity));
    }

    @Override
    public void onEntityRemoved(Entity entity) {
        entities.removeIf(sprite -> entity.equals(sprite.getEntity()));
    }
}
