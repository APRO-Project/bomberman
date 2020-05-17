package com.cyberbot.bomberman.core.models.defs;

public class BombDef {
    public final float power;
    public final float range;
    public final float detonationTime;

    public BombDef(float power, float range, float detonationTime) {
        this.power = power;
        this.range = range;
        this.detonationTime = detonationTime;
    }
}
