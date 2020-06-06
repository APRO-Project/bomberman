package com.cyberbot.bomberman.core.models.net.data;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.cyberbot.bomberman.core.models.defs.BombDef;
import com.cyberbot.bomberman.core.models.entities.BombEntity;

public class BombData extends EntityData<BombEntity> {
    private final int textureVariant;

    public BombData(long id, Vector2 position, int textureVariant) {
        super(id, position);
        this.textureVariant = textureVariant;
    }

    @Override
    public BombEntity createEntity(World world) {
        BombDef def = new BombDef(-1, -1, -1, textureVariant);
        BombEntity bombEntity = new BombEntity(world, def, id);
        bombEntity.setPositionRaw(position.toVector2());
        return bombEntity;
    }
}
