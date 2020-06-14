package com.cyberbot.bomberman.core.models.defs;

import com.cyberbot.bomberman.core.models.items.ItemType;

public class BombDef {
    public final float power;
    public final float powerDropOff;
    public final float range;
    public final float detonationTime;
    public final ItemType bombItemType;

    public BombDef(float power, float range,
                   float detonationTime, int playerTextureVariant, ItemType bombItemType) {
        this(power, power / range, range, detonationTime, bombItemType);
    }

    public BombDef(float power, float powerDropOff, float range,
                   float detonationTime, ItemType bombItemType) {
        if (!bombItemType.isBomb()) {
            throw new IllegalArgumentException("Item is not of bomb type: " + bombItemType);
        }

        this.power = power;
        this.range = range;
        this.detonationTime = detonationTime;
        this.powerDropOff = powerDropOff;
        this.bombItemType = bombItemType;
    }
}
