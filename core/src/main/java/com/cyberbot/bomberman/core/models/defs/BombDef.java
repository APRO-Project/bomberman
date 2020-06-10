package com.cyberbot.bomberman.core.models.defs;

import com.cyberbot.bomberman.core.models.items.ItemType;

public class BombDef {
    public final float power;
    public final float powerDropOff;
    public final float range;
    public final float detonationTime;
    public final int playerTextureVariant;
    public final ItemType bombItemType;

    public BombDef(float power, float range,
                   float detonationTime, int playerTextureVariant, ItemType bombItemType) {
        this(power, power / range, range, detonationTime, playerTextureVariant, bombItemType);
    }

    public BombDef(float power, float powerDropOff, float range,
                   float detonationTime, int playerTextureVariant, ItemType bombItemType) {
        if (bombItemType != ItemType.SMALL_BOMB && bombItemType != ItemType.MEDIUM_BOMB) {
            throw new IllegalArgumentException("Invalid bomb item type");
        }

        this.power = power;
        this.range = range;
        this.detonationTime = detonationTime;
        this.playerTextureVariant = playerTextureVariant;
        this.powerDropOff = powerDropOff;
        this.bombItemType = bombItemType;
    }
}
