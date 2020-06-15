package com.cyberbot.bomberman.screens.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.cyberbot.bomberman.core.models.items.ItemType;
import com.cyberbot.bomberman.sprites.GraphicsFactory;
import com.cyberbot.bomberman.utils.Atlas;

public class InventoryButton extends Actor {

    ItemType type;
    private int quantity;

    private final Stack mainWidget;
    private final Label labelQuantity;

    private final Button button;
    private final TextureRegionDrawable region;

    public InventoryButton(ItemType type, Skin skin) {
        this.type = type;
        quantity = 0;

        region = new TextureRegionDrawable(Atlas.getSkinAtlas().findRegion("placeholder"));

        Button.ButtonStyle buttonStyle = new Button.ButtonStyle();
        buttonStyle.checked = new TextureRegionDrawable(Atlas.getSkinAtlas().findRegion("button_checked"));
        button = new Button(buttonStyle);

        Label.LabelStyle style = new Label.LabelStyle();
        style.font = skin.get("default_font", BitmapFont.class);
        style.fontColor = skin.get("white", Color.class);
        style.background = new TextureRegionDrawable(Atlas.getSkinAtlas().findRegion("label_background"));

        labelQuantity = new Label(null, style);
        labelQuantity.setFontScale(0.5f);

        Container<Label> labelContainer = new Container<>(labelQuantity);
        labelContainer.align(Align.bottomRight).pad(1);

        mainWidget = new Stack();
        mainWidget.add(new Image(region));
        mainWidget.add(labelContainer);

        button.add(mainWidget).pad(8).align(Align.center);
    }

    public void updateDrawable() {
        if (type == null) {
            region.setRegion(Atlas.getSkinAtlas().findRegion("placeholder"));
        } else {
            region.setRegion(GraphicsFactory.getCollectibleTextureRegion(type));
        }
    }

    public void setQuantity(int updatedQuantity) {
        quantity = updatedQuantity;
        if (!isEmpty())
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

    public Button getButton() {
        return button;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        mainWidget.draw(batch, parentAlpha);
    }
}
