package com.cyberbot.bomberman.sprites;

import com.cyberbot.bomberman.models.entities.PlayerEntity;
import com.cyberbot.bomberman.models.factories.SpriteFactory;

public class PlayerSprite extends EntitySprite<PlayerEntity> {
    public PlayerSprite(PlayerEntity entity) {
        super(entity);
        sprite.set(SpriteFactory.createSprite(entity));
    }
}
