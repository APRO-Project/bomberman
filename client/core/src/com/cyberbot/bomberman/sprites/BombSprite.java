package com.cyberbot.bomberman.sprites;

import com.cyberbot.bomberman.core.models.entities.BombEntity;

public class BombSprite extends EntitySprite<BombEntity> {
    public static final int VARIANT_SMALL_RED = 0;
    public static final int VARIANT_MEDIUM_RED = 10;

    public BombSprite(BombEntity entity) {
        super(entity);
        this.sprite.set(GraphicsFactory.createSprite(entity));
    }
}
