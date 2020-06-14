package com.cyberbot.bomberman.core.models.net.data;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.cyberbot.bomberman.core.models.defs.PlayerDef;
import com.cyberbot.bomberman.core.models.entities.PlayerEntity;
import com.cyberbot.bomberman.core.models.items.Inventory;

public class PlayerData extends EntityData<PlayerEntity> {
    private final int textureVariant;
    private final PlayerEntity.FacingDirection facingDirection;
    private final float freezeTimeLeft;
    private final boolean frozen;
    private final Inventory inventory;
    private final int hp;

    public PlayerData(long id, Vector2 position, int textureVariant) {
        this(id, position, new Inventory(), textureVariant, PlayerEntity.FacingDirection.FRONT, 0, false, 100);
    }

    public PlayerData(long id, Vector2 position, Inventory inventory, int textureVariant,
                      PlayerEntity.FacingDirection facingDirection, float freezeTimeLeft, boolean frozen, int hp) {
        super(id, position);
        this.textureVariant = textureVariant;
        this.inventory = inventory;
        this.facingDirection = facingDirection;
        this.freezeTimeLeft = freezeTimeLeft;
        this.frozen = frozen;
        this.hp = hp;
    }

    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public PlayerEntity createEntity(World world) {
        PlayerDef def = new PlayerDef(textureVariant, inventory, hp);
        PlayerEntity playerEntity = new PlayerEntity(world, def, id);
        playerEntity.setPosition(position.toVector2());
        playerEntity.facingDirection = facingDirection;
        return playerEntity;
    }

    public int getHp() {
        return hp;
    }

    public PlayerEntity.FacingDirection getFacingDirection() {
        return facingDirection;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public float getFreezeTimeLeft() {
        return freezeTimeLeft;
    }
}
