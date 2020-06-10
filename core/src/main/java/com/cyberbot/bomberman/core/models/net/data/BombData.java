package com.cyberbot.bomberman.core.models.net.data;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.cyberbot.bomberman.core.models.defs.BombDef;
import com.cyberbot.bomberman.core.models.entities.BombEntity;
import com.cyberbot.bomberman.core.models.items.ItemType;

public class BombData extends EntityData<BombEntity> {
    private final int playerTextureVariant;
    private final ItemType bombItemType;

    public BombData(long id, Vector2 position, int playerTextureVariant, ItemType bombItemType) {
        super(id, position);
        this.playerTextureVariant = playerTextureVariant;
        this.bombItemType = bombItemType;
    }

    @Override
    public BombEntity createEntity(World world) {
        BombDef def = new BombDef(-1, -1, -1, -1, playerTextureVariant, bombItemType);
        BombEntity bombEntity = new BombEntity(world, def, id);
        bombEntity.setPosition(position.toVector2());
        return bombEntity;
    }
}
