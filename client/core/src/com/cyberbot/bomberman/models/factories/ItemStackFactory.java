package com.cyberbot.bomberman.models.factories;

import com.cyberbot.bomberman.models.items.ItemStack;
import com.cyberbot.bomberman.models.items.ItemType;
import com.cyberbot.bomberman.models.items.RefilingItemStack;

public class ItemStackFactory {
    public static ItemStack createStack(ItemType itemType) {
        switch (itemType) {
            case SMALL_BOMB:
                return new RefilingItemStack(itemType, 5);
            case UPGRADE_MOVEMENT_SPEED:
            case UPGRADE_REFILL_SPEED:
            case UPGRADE_ARMOR:
                return new ItemStack(itemType, 5);
        }

        throw new IllegalArgumentException("Invalid item type");
    }
}
