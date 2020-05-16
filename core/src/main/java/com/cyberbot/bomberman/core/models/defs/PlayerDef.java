package com.cyberbot.bomberman.core.models.defs;

import com.cyberbot.bomberman.core.models.items.Inventory;

public class PlayerDef {
    public float dragModifier;
    public float maxSpeedModifier;
    public int textureVariant;
    public final Inventory inventory = new Inventory();
}
