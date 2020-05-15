package com.cyberbot.bomberman.sprites;

import com.cyberbot.bomberman.models.entities.BombEntity;
import com.cyberbot.bomberman.models.factories.SpriteFactory;

public class BombSprite extends EntitySprite<BombEntity> {
    public BombSprite(BombEntity entity) {
        super(entity);
        this.sprite.set(SpriteFactory.createSprite(entity));
    }
}
