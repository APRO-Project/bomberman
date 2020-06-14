package com.cyberbot.bomberman.sprites;

import com.cyberbot.bomberman.core.models.entities.BombEntity;

public class BombSprite extends EntitySprite<BombEntity> {
    public static final int VARIANT_SMALL_RED = 0;
    public static final int VARIANT_BLACK = 10;
    public static final int VARIANT_NUKE = 100;

    public BombSprite(BombEntity entity) {
        super(entity);
        this.sprite.set(GraphicsFactory.createSprite(entity));
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        sprite.setRegion(GraphicsFactory.getBombTextureVariant(entity));
    }
}
