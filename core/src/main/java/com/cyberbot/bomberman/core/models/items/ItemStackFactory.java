package com.cyberbot.bomberman.core.models.items;

/**
 * Factory for {@link ItemStack ItemStacks}.
 */
public class ItemStackFactory {
    /**
     * Creates a default empty stack for a given item type.
     *
     * @param itemType The item.
     * @return An empty stack.
     * @throws IllegalArgumentException When the item type was not valid.
     */
    public static ItemStack createStack(ItemType itemType) {
        switch (itemType) {
            case SMALL_BOMB:
                return new RefilingItemStack(itemType, 3);
            case UPGRADE_MOVEMENT_SPEED:
            case UPGRADE_REFILL_SPEED:
            case UPGRADE_ARMOR:
                return new ItemStack(itemType, 5);
            default:
                throw new IllegalArgumentException("Invalid item type");
        }
    }
}
