package com.cyberbot.bomberman.models.items;

import com.cyberbot.bomberman.models.Updatable;

public class RefilingItemStack extends ItemStack implements Updatable {
    private float refillTime;
    private float quantity;

    public RefilingItemStack(ItemType type, float refillTime) {
        this(type, 1, refillTime);
    }

    public RefilingItemStack(ItemType type, int maxQuantity, float refillTime) {
        this(type, 0, maxQuantity, refillTime);
    }

    public RefilingItemStack(ItemType type, int quantity, int maxQuantity, float refillTime) {
        super(type, quantity, maxQuantity);

        this.refillTime = refillTime;
    }

    public float getRefillFraction() {
        return quantity - getQuantity();
    }

    @Override
    public int getQuantity() {
        return (int) quantity;
    }

    @Override
    public void update(float delta) {
        if (refillTime > 0 && quantity < maxQuantity) {
            quantity = Math.min(quantity + delta / refillTime, maxQuantity);
        }
    }
}
