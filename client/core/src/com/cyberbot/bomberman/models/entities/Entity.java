package com.cyberbot.bomberman.models.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.cyberbot.bomberman.models.Drawable;

import static com.cyberbot.bomberman.utils.Constants.PPM;

public abstract class Entity implements Drawable, Disposable {
    protected Body body;
    protected Sprite sprite;

    public Entity(World world) {
        createBody(world);
    }

    public abstract void createBody(World world);

    @Override
    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public void update(float delta) {
        Vector2 position = getPosition();
        sprite.setPosition(position.x - sprite.getWidth() / 2, position.y - sprite.getHeight() / 2);
    }

    @Override
    public void dispose() {
        if(sprite.getTexture() != null)
            sprite.getTexture().dispose();
    }

    public void setPosition(Vector2 position) {
        body.setTransform(position.x / PPM, position.y / PPM, 0);
    }

    public Vector2 getPosition() {
        Vector2 position = body.getPosition();
        return new Vector2(position.x * PPM, position.y * PPM);
    }
}
