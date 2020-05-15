package com.cyberbot.bomberman.models;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.cyberbot.bomberman.models.entities.Entity;

public class EntitySpritePair implements Updatable, Drawable, Disposable {
    private final Entity entity;
    private final Sprite sprite;

    public EntitySpritePair(Entity entity, Sprite sprite) {
        this.entity = entity;
        this.sprite = sprite;
    }

    public Entity getEntity() {
        return entity;
    }

    public Sprite getSprite() {
        return sprite;
    }

    @Override
    public void update(float delta) {
        Vector2 position = entity.getPosition();
        sprite.setPosition(position.x - sprite.getWidth() / 2, position.y - sprite.getHeight() / 2);
    }

    @Override
    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }

    @Override
    public void dispose() {
        Texture texture = sprite.getTexture();
        if (texture != null) {
            texture.dispose();
        }
    }
}
