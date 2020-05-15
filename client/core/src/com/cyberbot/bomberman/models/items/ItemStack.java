package com.cyberbot.bomberman.models.items;

import com.cyberbot.bomberman.models.Updatable;

public class ItemStack implements Updatable {
    private final ItemType type;
    private float quantity;
    private int maxQuantity;
    private float refillTime;

    public ItemStack(ItemType type) {
        this(type, -1);
    }

    public ItemStack(ItemType type, int maxQuantity) {
        this(type, 0, maxQuantity, 5);
    }

    public ItemStack(ItemType type, int quantity, int maxQuantity, float refillTime) {
        this.type = type;
        this.quantity = quantity;
        this.maxQuantity = maxQuantity;
        this.refillTime = refillTime;
    }

    public int getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(int maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    public int getQuantity() {
        return (int) quantity;
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

    public float getRefillFraction() {
        return quantity - getQuantity();
    }

    @Override
    public void update(float delta) {
        if (refillTime > 0 && quantity < maxQuantity) {
            quantity = Math.min(quantity + delta / refillTime, maxQuantity);
        }
    }
}
