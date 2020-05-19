package com.cyberbot.bomberman.core.models.net;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.cyberbot.bomberman.core.models.entities.CollectibleEntity;
import com.cyberbot.bomberman.core.models.items.ItemType;

public class CollectibleData extends EntityData<CollectibleEntity> {
    private final ItemType itemType;

    public CollectibleData(long id, Vector2 position, ItemType itemType) {
        super(id, position);
        this.itemType = itemType;
    }

    @Override
    public CollectibleEntity createEntity(World world) {
        CollectibleEntity entity = new CollectibleEntity(world, itemType, id);
        entity.setPosition(position.toVector2());
        return entity;
    }
}
