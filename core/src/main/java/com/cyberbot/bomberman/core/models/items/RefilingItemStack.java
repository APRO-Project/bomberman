package com.cyberbot.bomberman.core.models.items;

import com.cyberbot.bomberman.core.models.Updatable;

/**
 * An {@link ItemStack} that automatically refills as the time passes.
 */
public class RefilingItemStack extends ItemStack implements Updatable {
    /**
     * One item refilling time in seconds
     */
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

        if (refillTime < 0) {
            throw new IllegalArgumentException("Refill time has to be positive");
        }

        this.refillTime = refillTime;
    }

    public float getRefillFraction() {
        return quantityFraction;
    }

    @Override
    public void update(float delta) {
        if (refillTime > 0 && quantity < maxQuantity) {
            quantityFraction += delta / refillTime;

            while (quantityFraction >= 1 && addItem()) {
                quantityFraction--;
            }

            quantityFraction = Math.min(quantityFraction, 1);
        }
    }
}
