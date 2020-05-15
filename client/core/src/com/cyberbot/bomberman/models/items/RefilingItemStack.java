package com.cyberbot.bomberman.models.items;

import com.cyberbot.bomberman.models.Updatable;

public class RefilingItemStack extends ItemStack implements Updatable {
    private final float refillTime;
    private float quantityFraction;

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
        return quantityFraction;
    }

    @Override
    public void update(float delta) {
        if (refillTime > 0 && quantity < maxQuantity) {
            quantityFraction += delta / refillTime;

            if (quantityFraction >= 1) {
                quantityFraction--;
                addItem();
            }
        }
    }
}
