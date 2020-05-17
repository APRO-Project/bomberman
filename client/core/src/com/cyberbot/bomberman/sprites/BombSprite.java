package com.cyberbot.bomberman.sprites;

import com.cyberbot.bomberman.core.models.entities.BombEntity;

public class BombSprite extends EntitySprite<BombEntity> {
    public BombSprite(BombEntity entity) {
        super(entity);
        this.sprite.set(SpriteFactory.createSprite(entity));
    }
}
