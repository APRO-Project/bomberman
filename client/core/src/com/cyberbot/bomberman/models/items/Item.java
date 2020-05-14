package com.cyberbot.bomberman.models.items;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Disposable;
import com.cyberbot.bomberman.utils.Atlas;

public abstract class Item implements Disposable {
    protected Sprite sprite;

    public Item(String atlasPath) {
        sprite = Atlas.getInstance().createSprite(atlasPath);
    }

    public Sprite getSprite() {
        return sprite;
    }

    @Override
    public void dispose() {
        if(sprite.getTexture() != null)
            sprite.getTexture().dispose();
    }
}
