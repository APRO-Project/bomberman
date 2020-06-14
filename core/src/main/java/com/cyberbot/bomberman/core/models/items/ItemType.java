package com.cyberbot.bomberman.core.models.items;

import java.util.HashMap;
import java.util.Map;

public enum ItemType {
    // Usable items
    SMALL_BOMB(0),
    MEDIUM_BOMB(1),
    NUKE(2),
    FREEZER(3),

    // Collectible items
    UPGRADE_MOVEMENT_SPEED(100),
    UPGRADE_REFILL_SPEED(102),
    UPGRADE_ARMOR(103);

    private final int value;
    private static final Map<Integer, ItemType> map = new HashMap<>();

    ItemType(int value) {
        this.value = value;
    }

    static {
        for (ItemType type : ItemType.values()) {
            map.put(type.value, type);
        }
    }

    public int getValue() {
        return value;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isBomb() {
        return this == SMALL_BOMB || this == MEDIUM_BOMB || this == NUKE;
    }

    public static ItemType valueOf(int pageType) {
        return map.get(pageType);
    }
}
