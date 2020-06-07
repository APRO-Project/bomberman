package com.cyberbot.bomberman.screens.hud;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.utils.Align;
import com.cyberbot.bomberman.core.models.items.ItemType;

public class InventoryItemButton extends InventoryButton {

    private int quantity;

    private final Stack mainWidget;
    private final Label labelQuantity;

    public InventoryItemButton(ItemType type, Skin skin) {
        super(type);

        quantity = 0;

        labelQuantity = new Label(null, skin);
        labelQuantity.setFontScale(0.5f);

        Container<Label> labelContainer = new Container<>(labelQuantity);
        labelContainer.align(Align.bottomRight).pad(1);

        mainWidget = new Stack();
        mainWidget.add(button);
        mainWidget.add(labelContainer);
    }

    @Override
    public void makeEmpty() {
        super.makeEmpty();

        quantity = 0;
        labelQuantity.setText(null);
    }

    public void setQuantity(int updatedQuantity) {
        quantity = updatedQuantity;
        if(!isEmpty())
            labelQuantity.setText(String.valueOf(quantity));
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public Actor getMainWidget() {
        return mainWidget;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        mainWidget.draw(batch, parentAlpha);
    }
}
