package com.cyberbot.bomberman.models.defs;

import com.cyberbot.bomberman.models.items.Inventory;

public class PlayerDef {
    public float dragModifier;
    public float maxSpeedModifier;
    public int textureVariant;
    public final Inventory inventory = new Inventory();
}
