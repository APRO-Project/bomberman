package com.cyberbot.bomberman.models.items;

import com.cyberbot.bomberman.models.Updatable;
import com.cyberbot.bomberman.models.factories.ItemStackFactory;

import java.util.ArrayList;
import java.util.List;

public class Inventory implements Updatable {
    private final List<ItemStack> items;

    public Inventory() {
        this.items = new ArrayList<>();

        createEmptyStack(ItemType.UPGRADE_MOVEMENT_SPEED);
        createEmptyStack(ItemType.UPGRADE_ARMOR);
        createEmptyStack(ItemType.UPGRADE_REFILL_SPEED);

        addItem(ItemType.SMALL_BOMB);
    }

    public ItemType getItem(int index) {
        return items.get(index).getType();
    }

    public ItemType removeItem(int index) {
        final ItemStack stack = items.get(index);
        return stack.removeItem() ? stack.getType() : null;
    }

    public void collectItem(ItemType itemType) {
        switch (itemType) {
            case SMALL_BOMB:
                incrementMaxQuantity(itemType, true);
            default:
                addItem(itemType);
        }
    }

    public void incrementMaxQuantity(ItemType itemType, boolean addItem) {
        ItemStack stack = getStack(itemType);
        stack.incrementMaxQuantity();

        if (addItem) {
            stack.addItem();
        }
    }

    public boolean addItem(ItemType itemType) {
        return getStack(itemType).addItem();
    }

    public int getItemCount(ItemType itemType) {
        return getStack(itemType).getQuantity();
    }

    @Override
    public void update(float delta) {
        items.stream()
            .filter(i -> i instanceof Updatable)
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
            .filter(s -> s.getType() == itemType)
            .findFirst()
            .orElseGet(() -> createEmptyStack(itemType));
    }
}
