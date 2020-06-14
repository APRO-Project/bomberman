package com.cyberbot.bomberman.core.models.net.data;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.cyberbot.bomberman.core.models.entities.ExplosionEntity;

public class ExplosionData extends EntityData<ExplosionEntity> {

    public ExplosionData(long id, Vector2 position) {
        super(id, position);
    }

    @Override
    public ExplosionEntity createEntity(World world) {
        ExplosionEntity explosionEntity = new ExplosionEntity(world, id, ExplosionEntity.DO_NOT_DECAY);
        explosionEntity.setPosition(position.toVector2());
        return explosionEntity;
    }
}
