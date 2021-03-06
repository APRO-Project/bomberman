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
    @SuppressWarnings("DuplicateBranchesInSwitch") /* For better clarity */
    public static ItemStack createStack(ItemType itemType) {
        switch (itemType) {
            case SMALL_BOMB:
                return new RefilingItemStack(itemType, 2, false);
            case MEDIUM_BOMB:
                return new RefilingItemStack(itemType, 0, 5, true);
            case NUKE:
                return new RefilingItemStack(itemType, 0, 15, true);
            case FREEZER:
                return new ItemStack(itemType);
            case INSTA_BOOM:
                return new ItemStack(itemType);
            case UPGRADE_MOVEMENT_SPEED:
                return new ItemStack(itemType, 3);
            case UPGRADE_REFILL_SPEED:
                return new ItemStack(itemType, 4);
            case UPGRADE_ARMOR:
                return new ItemStack(itemType, 4);
            default:
                throw new IllegalArgumentException("Invalid item type");
        }
    }
}
