package com.cyberbot.bomberman.models.items;

import com.cyberbot.bomberman.models.Updatable;
import com.cyberbot.bomberman.models.factories.ItemStackFactory;

import java.util.ArrayList;
import java.util.List;

public class Inventory implements Updatable {
    private final List<ItemStack> items;

    public Inventory() {
        this.items = new ArrayList<>();
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

    @Override
    public void update(float delta) {
        // TODO: Multiply by player's regen time upgrade to refill quicker
        items.stream()
                .filter(i -> i instanceof Updatable)
                .forEach(i -> ((Updatable) i).update(delta));
    }

    private ItemStack getStack(ItemType itemType) {
        return items.stream()
                .filter(s -> s.getType() == itemType)
                .findFirst()
                .orElseGet(() -> {
                    ItemStack s = ItemStackFactory.createStack(itemType);
                    items.add(s);
                    return s;
                });
    }
}
