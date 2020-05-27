package com.cyberbot.bomberman.screens.hud;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.cyberbot.bomberman.core.models.entities.PlayerEntity;
import com.cyberbot.bomberman.core.models.items.ItemStack;
import com.cyberbot.bomberman.core.models.items.ItemType;
import com.cyberbot.bomberman.utils.Atlas;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.*;
import java.util.stream.Collectors;

import static com.cyberbot.bomberman.core.utils.Constants.PPM;

public final class InventoryView extends Table {

    private static final List<ItemType> ITEMS;
    private static final List<ItemType> EFFECTS;

    static {
        ITEMS = Arrays.asList(
            ItemType.SMALL_BOMB
        );
        EFFECTS = Arrays.asList(
            ItemType.UPGRADE_ARMOR,
            ItemType.UPGRADE_MOVEMENT_SPEED,
            ItemType.UPGRADE_REFILL_SPEED
        );
    }

    private final InventoryButton[] effectButtons;
    private final InventoryItemButton[] itemButtons;

    private final PlayerEntity player;

    public InventoryView(PlayerEntity player, Skin skin) {
        effectButtons = new InventoryButton[5];
        itemButtons = new InventoryItemButton[5];

        this.player = player;

        init(skin);
    }

    public void init(Skin skin) {
        Label inventoryLabel = new Label("Inventory", skin);
        Table effects = new Table();
        Table items = new Table();

        for (int i = 0; i < 5; ++i) {
            effectButtons[i] = new InventoryButton(null);
            itemButtons[i] = new InventoryItemButton(null, skin);

            effects.add(effectButtons[i].getMainWidget()).padBottom(PPM / 2).row();
            items.add(itemButtons[i].getMainWidget()).padBottom(PPM / 2).row();
        }

        this.add(inventoryLabel).colspan(3).padTop(5).padBottom(PPM / 2).row();
        this.add(effects).expandX();

        NinePatch separator = new NinePatch(Atlas.getSkinAtlas().findRegion("separator"));
        this.add(new Image(separator))
            .expandY()
            .padBottom(PPM / 2)
            .minWidth(2)
            .prefWidth(2)
            .prefHeight(999);

        this.add(items).expandX();
//        inventoryView.setDebug(true);
    }

    private boolean scanForInventoryChanges() {
        boolean change = false;

        final ArrayList<ItemStack> effectStacks = player.getInventory().getItems().stream()
            .filter(i -> EFFECTS.contains(i.getItemType()))
            .collect(Collectors.toCollection(ArrayList::new));

        final ArrayList<ItemStack> itemStacks = player.getInventory().getItems().stream()
            .filter(i -> ITEMS.contains(i.getItemType()))
            .collect(Collectors.toCollection(ArrayList::new));

        // Effects
        for (ItemStack stack : effectStacks) {
            final Optional<InventoryButton> correspondingButton = Arrays.stream(effectButtons)
                .filter(btn -> btn.type == stack.getItemType())
                .findFirst();

            if (!correspondingButton.isPresent() && stack.getQuantity() != 0) {
                final int slot = getNextEmptyEffectSlot();
                if (slot != -1) {
                    effectButtons[slot].type = stack.getItemType();
                    effectButtons[slot].updateDrawable();
                    change = true;
                }
            } else if (correspondingButton.isPresent() && stack.getQuantity() == 0) {
                correspondingButton.get().makeEmpty();
                correspondingButton.get().updateDrawable();
                change = true;
            }
        }

        // Items
        for (ItemStack stack : itemStacks) {
            final Optional<InventoryItemButton> correspondingButton = Arrays.stream(itemButtons)
                .filter(btn -> btn.type == stack.getItemType())
                .findFirst();

            if (correspondingButton.isPresent()) {
                correspondingButton.get().setQuantity(stack.getQuantity());
            } else {
                final int slot = getNextEmptyItemSlot();
                if (slot != -1) {
                    itemButtons[slot].type = stack.getItemType();
                    itemButtons[slot].setQuantity(stack.getQuantity());
                    itemButtons[slot].updateDrawable();
                    change = true;
                }
            }
        }

        return change;
    }

    private void rearrangeButtons() {
        final List<ItemType> partitionedEffects = Arrays.stream(effectButtons)
            .collect(Collectors.partitioningBy(InventoryButton::isEmpty))
            .values().stream()
            .flatMap(List::stream)
            .map(btn -> btn.type)
            .collect(Collectors.toList());

        final List<ImmutablePair<ItemType, Integer>> partitionedItems = Arrays.stream(itemButtons)
            .collect(Collectors.partitioningBy(InventoryItemButton::isEmpty))
            .values().stream()
            .flatMap(List::stream)
            .map(btn -> new ImmutablePair<>(btn.type, btn.getQuantity()))
            .collect(Collectors.toList());

        for (int i = 0; i < effectButtons.length; ++i) {
            effectButtons[i].type = partitionedEffects.get(i);
            effectButtons[i].updateDrawable();

            itemButtons[i].type = partitionedItems.get(i).getKey();
            itemButtons[i].setQuantity(partitionedItems.get(i).getValue());
        }
    }

    private int getNextEmptyEffectSlot() {
        for (int i = 0; i < effectButtons.length; ++i) {
            if (effectButtons[i].isEmpty())
                return i;
        }

        return -1;
    }

    private int getNextEmptyItemSlot() {
        for (int i = 0; i < itemButtons.length; ++i) {
            if (itemButtons[i].isEmpty())
                return i;
        }

        return -1;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (scanForInventoryChanges())
            rearrangeButtons();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }
}
