package com.cyberbot.bomberman.models.items;

import java.util.List;

public class Inventory {
    private List<ItemStack> items;

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
}
