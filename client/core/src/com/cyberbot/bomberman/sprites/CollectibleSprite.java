package com.cyberbot.bomberman.sprites;

import com.cyberbot.bomberman.models.entities.CollectibleEntity;
import com.cyberbot.bomberman.models.factories.SpriteFactory;

public class CollectibleSprite extends EntitySprite<CollectibleEntity> {
    public CollectibleSprite(CollectibleEntity entity) {
        super(entity);
        sprite.set(SpriteFactory.createSprite(entity));
    }
}
