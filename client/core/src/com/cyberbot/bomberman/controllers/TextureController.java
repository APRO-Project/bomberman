package com.cyberbot.bomberman.controllers;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.cyberbot.bomberman.models.Drawable;
import com.cyberbot.bomberman.models.EntitySpritePair;
import com.cyberbot.bomberman.models.TileSpritePair;
import com.cyberbot.bomberman.models.Updatable;
import com.cyberbot.bomberman.models.entities.Entity;
import com.cyberbot.bomberman.models.factories.SpriteFactory;
import com.cyberbot.bomberman.models.tiles.Tile;
import com.cyberbot.bomberman.models.tiles.TileMap;

import java.util.ArrayList;
import java.util.List;

public class TextureController implements Drawable, Updatable, GameStateController.ChangeListener, TileMap.ChangeListener {
    private final List<EntitySpritePair> entities;
    private final List<TileSpritePair> base;
    private final List<TileSpritePair> floor;
    private final List<TileSpritePair> walls;

    public TextureController(TileMap map) {
        map.addListener(this);
        this.entities = new ArrayList<>();
        this.base = TileSpritePair.fromTileLayer(map.getBase());
        this.floor = TileSpritePair.fromTileLayer(map.getFloor());
        this.walls = TileSpritePair.fromTileLayer(map.getWalls());
    }

    @Override
    public void draw(SpriteBatch batch) {
        base.forEach(pair -> pair.draw(batch));
        floor.forEach(pair -> pair.draw(batch));
        walls.forEach(pair -> pair.draw(batch));
        entities.forEach(pair -> pair.draw(batch));
    }

    @Override
    public void update(float delta) {
        entities.forEach(pair -> pair.update(delta));
    }

    @Override
    public void onEntityAdded(Entity entity) {
        Sprite sprite = SpriteFactory.createSprite(entity);
        entities.add(new EntitySpritePair(entity, sprite));
    }

    @Override
    public void onEntityRemoved(Entity entity) {
        EntitySpritePair pair = entities.stream()
                .filter(p -> p.getEntity().equals(entity))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Entity not present in TextureController"));
        pair.dispose();
        entities.remove(pair);
    }

    @Override
    public void onWallAdded(Tile tile) {
        walls.add(new TileSpritePair(tile));
    }

    @Override
    public void onWallRemoved(Tile tile) {
        TileSpritePair pair = walls.stream()
                .filter(p -> p.getTile().equals(tile))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("WallTile not present in TextureController"));
        pair.dispose();
        walls.remove(pair);
    }
}
