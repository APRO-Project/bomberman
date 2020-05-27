package com.cyberbot.bomberman.screens.hud;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.cyberbot.bomberman.core.models.items.ItemType;
import com.cyberbot.bomberman.utils.Atlas;

public class InventoryButton extends Actor {

    protected ImageButton button;
    protected TextureRegionDrawable region;
    ItemType type;

    public InventoryButton(ItemType type) {
        this.type = type;

        region = new TextureRegionDrawable(Atlas.getSkinAtlas().findRegion("placeholder"));

        ImageButton.ImageButtonStyle buttonStyle = new ImageButton.ImageButtonStyle();
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
    }

    public boolean isEmpty() {
        return type == null;
    }

    public Actor getMainElement() {
        return button;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        button.draw(batch, parentAlpha);
    }
}
