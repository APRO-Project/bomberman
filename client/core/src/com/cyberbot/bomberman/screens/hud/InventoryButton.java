package com.cyberbot.bomberman.screens.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.cyberbot.bomberman.core.models.items.ItemType;
import com.cyberbot.bomberman.utils.Atlas;

public class InventoryButton extends Actor {

    ItemType type;
    private int quantity;

    private final Stack mainWidget;
    private final Label labelQuantity;

    private final ImageButton button;
    private final TextureRegionDrawable region;

    public InventoryButton(ItemType type, Skin skin) {
        this.type = type;
        quantity = 0;

        region = new TextureRegionDrawable(Atlas.getSkinAtlas().findRegion("placeholder"));

        ImageButton.ImageButtonStyle buttonStyle = new ImageButton.ImageButtonStyle();
        buttonStyle.imageUp = region;
        buttonStyle.checked = new TextureRegionDrawable(Atlas.getSkinAtlas().findRegion("button_checked"));

        button = new ImageButton(buttonStyle);

        Label.LabelStyle style = new Label.LabelStyle();
        style.font = skin.get("default_font", BitmapFont.class);
        style.fontColor = skin.get("white", Color.class);
        style.background = new TextureRegionDrawable(Atlas.getSkinAtlas().findRegion("label_background"));

        labelQuantity = new Label(null, style);
        labelQuantity.setFontScale(0.5f);

        Container<Label> labelContainer = new Container<>(labelQuantity);
        labelContainer.align(Align.bottomRight)
            .pad(1);

        mainWidget = new Stack();
        mainWidget.add(button);
        mainWidget.add(labelContainer);
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

    public void setQuantity(int updatedQuantity) {
        quantity = updatedQuantity;
        if(!isEmpty())
            labelQuantity.setText(String.valueOf(quantity));
    }

    public int getQuantity() {
        return quantity;
    }

    public void makeEmpty() {
        type = null;
        quantity = 0;
        labelQuantity.setText(null);
    }

    public boolean isEmpty() {
        return type == null;
    }

    public Actor getMainWidget() {
        return mainWidget;
    }

    public ImageButton getButton() {
        return button;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        mainWidget.draw(batch, parentAlpha);
    }
}
