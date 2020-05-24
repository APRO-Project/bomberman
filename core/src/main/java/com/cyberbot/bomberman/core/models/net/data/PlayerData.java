package com.cyberbot.bomberman.core.models.net.data;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.cyberbot.bomberman.core.models.defs.PlayerDef;
import com.cyberbot.bomberman.core.models.entities.PlayerEntity;
import com.cyberbot.bomberman.core.models.items.Inventory;

public class PlayerData extends EntityData<PlayerEntity> {
    private final int textureVariant;
    private final Inventory inventory;

    public PlayerData(long id, Vector2 position, Inventory inventory, int textureVariant) {
        super(id, position);
        this.textureVariant = textureVariant;
        this.inventory = inventory;
    }

    @Override
    public PlayerEntity createEntity(World world) {
        PlayerDef def = new PlayerDef(textureVariant, inventory);
        PlayerEntity playerEntity = new PlayerEntity(world, def, id);
        playerEntity.setPosition(position.toVector2());
        return playerEntity;
    }
}
