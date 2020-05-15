package com.cyberbot.bomberman.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.cyberbot.bomberman.models.Drawable;
import com.cyberbot.bomberman.models.factories.SpriteFactory;
import com.cyberbot.bomberman.models.tiles.Tile;
import com.cyberbot.bomberman.models.tiles.TileMapLayer;

import java.util.List;
import java.util.stream.Collectors;

public class TileSprite implements Drawable, Disposable {
    private final Tile tile;
    private final Sprite sprite;

    public TileSprite(Tile tile) {
        this.tile = tile;
        this.sprite = SpriteFactory.createSprite(tile);

        Vector2 position = tile.getPosition();
        this.sprite.setPosition(position.x, position.y);
    }


    @Override
    public void dispose() {
        Texture texture = sprite.getTexture();
        if(texture != null) {
            texture.dispose();
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public static List<TileSprite> fromTileLayer(TileMapLayer layer) {
        return layer.stream().map(TileSprite::new).collect(Collectors.toList());
    }

    public Tile getTile() {
        return tile;
    }
}
