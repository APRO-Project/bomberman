package com.cyberbot.bomberman.core.models.items;

import com.cyberbot.bomberman.core.models.Updatable;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains information about a player's inventory
 *
 * @see ItemStack
 */
public class Inventory implements Updatable {
    private final List<ItemStack> items;

    /**
     * Creates a new inventory with default stacks initialized.
     */
    public Inventory() {
        this.items = new ArrayList<>();

        createEmptyStack(ItemType.UPGRADE_MOVEMENT_SPEED);
        createEmptyStack(ItemType.UPGRADE_ARMOR);
        createEmptyStack(ItemType.UPGRADE_REFILL_SPEED);

        addItem(ItemType.SMALL_BOMB);
    }

    public ItemType getItem(int index) {
        return items.get(index).getItemType();
    }

    /**
     * Removes an item from a given stack if it exists in the player's inventory.
     *
     * @param itemType The item.
     * @return true if the inventory has been modified
     */
    public boolean removeItem(ItemType itemType) {
        final ItemStack stack = items.stream()
            .filter(s -> s.getItemType() == itemType)
            .findFirst()
            .orElse(null);

        if (stack == null) {
            return false;
        }

        return stack.removeItem();
    }

    /**
     * Increments the max quantity for a given item stack.
     *
     * @param itemType The item.
     * @param addItem  Whether an item should also be added to fill the newly created space.
     */
    public void incrementMaxQuantity(ItemType itemType, boolean addItem) {
        ItemStack stack = getStack(itemType);
        stack.incrementMaxQuantity();

        if (addItem) {
            stack.addItem();
        }
    }

    /**
     * Adds a new item to the given item stack.
     *
     * @param itemType The item.
     * @return true if the stack has been modified.
     */
    public boolean addItem(ItemType itemType) {
        return getStack(itemType).addItem();
    }

    /**
     * Returns the quantity of a given stack if it exists in the inventory or 0 if it does not.
     *
     * @param itemType The item.
     * @return the quantity of a given stack if it exists in the inventory or 0 if it does not.
     */
    public int getItemCount(ItemType itemType) {
        return items.stream()
            .filter(s -> s.getItemType() == itemType)
            .findFirst()
            .map(ItemStack::getQuantity)
            .orElse(0);
    }

    @Override
    public void update(float delta) {
        items.stream()
            .filter(i -> i instanceof Updatable)
            // TODO: Remove the refill speed multiplier
            .forEach(i -> ((Updatable) i).update(delta * getRefillSpeedMultiplier()));
    }

    public float getMovementSpeedMultiplier() {
        return (float) Math.pow(
            Upgrade.MOVEMENT_SPEED_MULTIPLIER,
            getItemCount(ItemType.UPGRADE_MOVEMENT_SPEED));
    }

    public float getArmorMultiplier() {
        return (float) Math.pow(
            Upgrade.ARMOR_MULTIPLIER,
            getItemCount(ItemType.UPGRADE_ARMOR));
    }

    public float getRefillSpeedMultiplier() {
        return (float) Math.pow(
            Upgrade.REFILL_SPEED_MULTIPLIER,
            getItemCount(ItemType.UPGRADE_REFILL_SPEED));
    }

    private ItemStack createEmptyStack(ItemType itemType) {
        ItemStack s = ItemStackFactory.createStack(itemType);
        items.add(s);
        return s;
    }

    private ItemStack getStack(ItemType itemType) {
        return items.stream()
            .filter(s -> s.getItemType() == itemType)
            .findFirst()
            .orElseGet(() -> createEmptyStack(itemType));
    }

    public int getStackItemQuantity(ItemType itemType) {
        return items.stream()
            .filter(s -> s.getItemType() == itemType)
            .findFirst()
            .map(s -> s.quantity).orElse(-1);
    }

    public List<ItemStack> getItems() {
        return items;
    }
}
