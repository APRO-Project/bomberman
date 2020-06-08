package com.cyberbot.bomberman.core.models.items;

import java.util.HashMap;
import java.util.Map;

public enum ItemType {
    // Usable items
    SMALL_BOMB(0),

    // Collectible items
    UPGRADE_MOVEMENT_SPEED(1),
    UPGRADE_REFILL_SPEED(2),
    UPGRADE_ARMOR(3);

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

    public static ItemType valueOf(int pageType) {
        return map.get(pageType);
    }

    public int getValue() {
        return value;
    }
}
