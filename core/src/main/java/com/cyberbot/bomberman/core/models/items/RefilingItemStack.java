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
    private final boolean affectedByModifier;
    private float quantityFraction;

    // Mmmm, yes. Java's overloads, maybe add some more?
    public RefilingItemStack(ItemType type, float refillTime) {
        this(type, refillTime, true);
    }

    public RefilingItemStack(ItemType type, float refillTime, boolean affectedByModifier) {
        this(type, 1, refillTime, affectedByModifier);
    }

    public RefilingItemStack(ItemType type, int maxQuantity, float refillTime, boolean affectedByModifier) {
        this(type, 0, maxQuantity, refillTime, affectedByModifier);
    }

    public RefilingItemStack(ItemType type, int quantity, int maxQuantity,
                             float refillTime, boolean affectedByModifier) {
        super(type, quantity, maxQuantity);

        if (refillTime < 0) {
            throw new IllegalArgumentException("Refill time has to be positive");
        }

        this.refillTime = refillTime;
        this.affectedByModifier = affectedByModifier;
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


    /**
     * Returns a float in range 0-1 that represents a progress of the refiling.
     *
     * @return a float in range 0-1 that represents a progress of the refiling.
     */
    public float getRefillFraction() {
        return quantityFraction;
    }

    /**
     * Whether this item stack's refill time should be affected by any modifiers
     *
     * @return <code>true</code> if this item stack's refill time should be affected by any modifiers
     */
    public boolean isAffectedByModifier() {
        return affectedByModifier;
    }
}
