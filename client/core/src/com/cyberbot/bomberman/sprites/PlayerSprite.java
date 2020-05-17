package com.cyberbot.bomberman.sprites;

import com.cyberbot.bomberman.core.models.entities.PlayerEntity;

public class PlayerSprite extends EntitySprite<PlayerEntity> {
    public PlayerSprite(PlayerEntity entity) {
        super(entity);
        sprite.set(SpriteFactory.createSprite(entity));
    }
}
