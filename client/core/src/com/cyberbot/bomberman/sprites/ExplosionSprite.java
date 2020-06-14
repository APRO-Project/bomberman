package com.cyberbot.bomberman.sprites;

import com.cyberbot.bomberman.core.models.entities.ExplosionEntity;

public class ExplosionSprite extends EntitySprite<ExplosionEntity> {

    public ExplosionSprite(ExplosionEntity entity) {
        super(entity);
        sprite.set(GraphicsFactory.getExplosionSprite());
    }
}
