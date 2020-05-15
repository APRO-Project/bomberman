package com.cyberbot.bomberman.models.items;

public class ItemStack {
    public static final int INFINITE_QUANTITY = -1;

    protected final ItemType type;
    protected int quantity;
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

    public boolean removeItem() {
        if (quantity == -1) {
            return true;
        }

        if (quantity >= 1) {
            quantity--;
            return true;
        }

        return false;
    }

    public boolean addItem() {
        if (quantity < maxQuantity) {
            quantity++;
            return true;
        }

        return false;
    }

    public ItemType getType() {
        return type;
    }
}
