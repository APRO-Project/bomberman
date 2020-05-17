package com.cyberbot.bomberman.core.models.items;

/**
 * Contains information about the quantity and max quantity of an {@link ItemType}.
 */
public class ItemStack {
    public static final int INFINITE_QUANTITY = -1;

    protected final ItemType type;

    /**
     * The amount of items this stack holds.
     * When set to {@link #INFINITE_QUANTITY} adding and removing items does not change the quantity.
     */
    protected int quantity;

    /**
     * The maximum amount of items this stack can hold.
     * When set to {@link #INFINITE_QUANTITY} the stack does not have a limit.
     * (note the integer limit of 2^32-1)
     */
    protected int maxQuantity;

    public ItemStack(ItemType type) {
        this(type, INFINITE_QUANTITY);
    }

    public ItemStack(ItemType type, int maxQuantity) {
        this(type, 0, maxQuantity);
    }

    public ItemStack(ItemType type, int quantity, int maxQuantity) {
        this.type = type;
        this.quantity = quantity;
        this.maxQuantity = maxQuantity;
    }

    public void incrementMaxQuantity() {
        maxQuantity++;
    }

    public int getQuantity() {
        return quantity;
    }

    /**
     * Removes an item from the stack.
     *
     * @return true if the stack contained an item to remove.
     */
    public boolean removeItem() {
        if (quantity == INFINITE_QUANTITY) {
            return true;
        }

        if (quantity >= 1) {
            quantity--;
            return true;
        }

        return false;
    }

    /**
     * Adds an item to the stack.
     *
     * @return true if the stack had space to add the item.
     */
    public boolean addItem() {
        if (quantity == INFINITE_QUANTITY) {
            return true;
        }

        if (maxQuantity == INFINITE_QUANTITY || quantity < maxQuantity) {
            quantity++;
            return true;
        }

        return false;
    }

    public ItemType getItemType() {
        return type;
    }
}
