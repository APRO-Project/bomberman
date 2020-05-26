package com.cyberbot.bomberman.screens.hud;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.cyberbot.bomberman.core.models.items.ItemType;
import com.cyberbot.bomberman.utils.Atlas;

public class InventoryItemButton extends Actor {

    final ImageButton button;
    final ImageButton.ImageButtonStyle buttonStyle;
    final TextureRegionDrawable region;
    ItemType type;
    int quantity;

    public InventoryItemButton(ItemType type) {
        this.type = type;
        quantity = 0;

        region = new TextureRegionDrawable(Atlas.getSkinAtlas().findRegion("placeholder"));

        buttonStyle = new ImageButton.ImageButtonStyle();
        buttonStyle.imageUp = region;

        button = new ImageButton(buttonStyle);
    }

    public void updateDrawable() {
        if(type == null) {
            region.setRegion(Atlas.getSkinAtlas().findRegion("placeholder"));
            return;
        }

        switch (type) {
            case UPGRADE_MOVEMENT_SPEED:
                region.setRegion(Atlas.getInstance().findRegion("ArrowFast"));
                break;
            case SMALL_BOMB:
                region.setRegion(Atlas.getInstance().findRegion("DynamiteStatic"));
                break;
            case UPGRADE_ARMOR:
                region.setRegion(Atlas.getInstance().findRegion("Shield"));
                break;
            case UPGRADE_REFILL_SPEED:
                region.setRegion(Atlas.getInstance().findRegion("Player_bbb_idle_back"));
                break;
            default:
                region.setRegion(Atlas.getSkinAtlas().findRegion("placeholder"));
                break;
        }
    }

    public void makeEmpty() {
        type = null;
        quantity = 0;
    }

    public boolean isEmpty() {
        return type == null;
    }

    public void setType(ItemType type) {
        this.type = type;
    }

    public void setQuantity(int updatedQuantity) {
        if(updatedQuantity == 0) {
            makeEmpty();
            updateDrawable();
        }
        if (quantity == 0 && updatedQuantity != 0) {
            if (updatedQuantity >= 0) {
                updateDrawable();
                quantity = updatedQuantity;
            }
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }
}
