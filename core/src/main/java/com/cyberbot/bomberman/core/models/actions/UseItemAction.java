package com.cyberbot.bomberman.core.models.actions;

import com.cyberbot.bomberman.core.models.items.ItemType;

public class UseItemAction extends Action {
    private final ItemType itemType;

    public UseItemAction(ItemType itemType) {
        super(Type.USE_ITEM);
        this.itemType = itemType;
    }

    public ItemType getItemType() {
        return itemType;
    }
}
