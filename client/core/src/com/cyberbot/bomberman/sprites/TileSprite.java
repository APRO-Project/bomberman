package com.cyberbot.bomberman.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.cyberbot.bomberman.core.models.tiles.Tile;
import com.cyberbot.bomberman.models.Drawable;

public class TileSprite implements Drawable {
    private final Tile tile;
    private final Sprite sprite;

    public TileSprite(Tile tile) {
        this.tile = tile;
        this.sprite = SpriteFactory.createSprite(tile);

        Vector2 position = tile.getPositionRaw();
        this.sprite.setPosition(position.x - sprite.getWidth() / 2, position.y - sprite.getHeight() / 2);
    }


    @Override
    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public Tile getTile() {
        return tile;
    }
}
