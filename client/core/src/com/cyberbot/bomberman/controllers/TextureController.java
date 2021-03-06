package com.cyberbot.bomberman.controllers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.cyberbot.bomberman.core.controllers.WorldChangeListener;
import com.cyberbot.bomberman.core.models.Updatable;
import com.cyberbot.bomberman.core.models.entities.Entity;
import com.cyberbot.bomberman.core.models.tiles.Tile;
import com.cyberbot.bomberman.core.models.tiles.TileMap;
import com.cyberbot.bomberman.models.Drawable;
import com.cyberbot.bomberman.sprites.EntitySprite;
import com.cyberbot.bomberman.sprites.GraphicsFactory;
import com.cyberbot.bomberman.sprites.TileSprite;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to draw the map and all entities present on the map.
 * listens for map and game state changes and updates it's sprite's accordingly.
 */
public final class TextureController implements Drawable, Updatable, WorldChangeListener, TileMap.ChangeListener {
    private final List<EntitySprite<?>> entities;
    private final List<TileSprite> base;
    private final List<TileSprite> floor;
    private final List<TileSprite> walls;

    public TextureController(TileMap map) {
        map.addListener(this);
        this.entities = new ArrayList<>();
        this.base = GraphicsFactory.createTilesFromMapLayer(map.getBase());
        this.floor = GraphicsFactory.createTilesFromMapLayer(map.getFloor());
        this.walls = GraphicsFactory.createTilesFromMapLayer(map.getWalls());
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
        EntitySprite<?> sprite = GraphicsFactory.createEntitySprite(entity);
        entities.add(0, sprite);
    }

    @Override
    public void onEntityRemoved(Entity entity) {
        entities.removeIf(sprite -> entity.equals(sprite.getEntity()));
    }
}
