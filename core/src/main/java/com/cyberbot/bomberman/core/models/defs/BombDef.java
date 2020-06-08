package com.cyberbot.bomberman.core.models.defs;

public class BombDef {
    public final float power;
    public final float powerDropOff;
    public final float range;
    public final float detonationTime;
    public final int textureVariant;

    public BombDef(float power, float powerDropOff, float range, float detonationTime, int textureVariant) {
        this.power = power;
        this.range = range;
        this.detonationTime = detonationTime;
        this.textureVariant = textureVariant;
        this.powerDropOff = powerDropOff;
    }
}
