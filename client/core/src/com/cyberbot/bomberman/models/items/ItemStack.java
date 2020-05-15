package com.cyberbot.bomberman.models.items;

public class ItemStack {
    private final ItemType type;
    private int quantity;
    private int maxQuantity;
    protected boolean isRefillable;

    public ItemStack(ItemType type) {
        this(type, -1);
    }

    public ItemStack(ItemType type, int maxQuantity) {
        this(type, 0, maxQuantity, false);
    }

    public ItemStack(ItemType type, int quantity, int maxQuantity, boolean isRefillable) {
        this.type = type;
        this.quantity = quantity;
        this.maxQuantity = maxQuantity;
        this.isRefillable = isRefillable;
    }

    public int getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(int maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean removeItem() {
        if(quantity == -1) {
            return true;
        }

        if(quantity > 0) {
            quantity--;
            return true;
        }

        return false;
    }

    public boolean addItem() {
        if(quantity < maxQuantity) {
            quantity++;
            return true;
        }

        return false;
    }

    public ItemType getType() {
        return type;
    }
}
