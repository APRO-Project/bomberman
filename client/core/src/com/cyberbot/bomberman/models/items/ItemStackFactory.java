package com.cyberbot.bomberman.models.items;

public class ItemStackFactory {
    public static ItemStack createStack(ItemType itemType) {
        switch (itemType) {
            case SMALL_BOMB:
                return new ItemStack(itemType, 0, 1, true);
        }

        throw new IllegalArgumentException("Invalid item type");
    }
}
