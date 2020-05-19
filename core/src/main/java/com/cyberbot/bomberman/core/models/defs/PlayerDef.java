package com.cyberbot.bomberman.core.models.defs;

import com.cyberbot.bomberman.core.models.items.Inventory;

public class PlayerDef {
    public float dragModifier = 1;
    public float maxSpeedModifier = 1;
    public int textureVariant;
    public Inventory inventory;

    public PlayerDef(int textureVariant) {
        this(textureVariant, new Inventory());
    }

    public PlayerDef(int textureVariant, Inventory inventory) {
        this.textureVariant = textureVariant;
        this.inventory = inventory;
    }
}
