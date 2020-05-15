package com.cyberbot.bomberman.models.items;

import com.cyberbot.bomberman.models.Updatable;
import com.cyberbot.bomberman.models.factories.ItemStackFactory;

import java.util.ArrayList;
import java.util.List;

public class Inventory implements Updatable {
    private List<ItemStack> items;

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

    public boolean addItem(ItemType itemType) {
        ItemStack stack = items.stream()
                .filter(s -> s.getType() == itemType)
                .findFirst()
                .orElse(null);

        if(stack == null) {
            stack = ItemStackFactory.createStack(itemType);
            items.add(stack);
        }

        return stack.addItem();
    }

    @Override
    public void update(float delta) {
        // TODO: Multiply by player's regen time upgrade to refill quicker
        items.forEach(i -> i.update(delta));
    }
}
