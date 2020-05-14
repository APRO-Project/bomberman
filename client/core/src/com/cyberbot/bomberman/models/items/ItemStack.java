package com.cyberbot.bomberman.models.items;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.cyberbot.bomberman.models.Drawable;

public class ItemStack implements Drawable {
    private Item item;
    private int quantity;

    public ItemStack(Item item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public ItemStack(Item item) {
        this(item, 0);
    }

    @Override
    public void draw(SpriteBatch batch) {

    }
}
