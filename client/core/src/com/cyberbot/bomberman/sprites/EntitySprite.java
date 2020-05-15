package com.cyberbot.bomberman.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.cyberbot.bomberman.models.Drawable;
import com.cyberbot.bomberman.models.Updatable;
import com.cyberbot.bomberman.models.entities.Entity;

public abstract class EntitySprite<E extends Entity> implements Updatable, Drawable {
    protected final E entity;
    protected final Sprite sprite;

    public EntitySprite(E entity) {
        this.entity = entity;
        this.sprite = new Sprite();
    }

    public E getEntity() {
        return entity;
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
}
