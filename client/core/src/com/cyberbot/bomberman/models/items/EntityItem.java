package com.cyberbot.bomberman.models.items;

import com.badlogic.gdx.physics.box2d.World;
import com.cyberbot.bomberman.models.entities.Entity;

public abstract class EntityItem<E extends Entity> extends Item {

    public EntityItem(String atlasPath) {
        super(atlasPath);
    }

    public abstract E createEntity(World world, String texturePath);
}
