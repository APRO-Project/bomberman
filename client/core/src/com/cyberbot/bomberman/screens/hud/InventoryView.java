package com.cyberbot.bomberman.screens.hud;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.cyberbot.bomberman.core.models.entities.PlayerEntity;
import com.cyberbot.bomberman.core.models.items.ItemStack;
import com.cyberbot.bomberman.core.models.items.ItemType;
import com.cyberbot.bomberman.utils.Atlas;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.cyberbot.bomberman.core.utils.Constants.PPM;

public final class InventoryView extends Table {

    private static final int MAX_SLOTS = 5;

    private final InventoryButton[] effectButtons;
    private final InventoryButton[] itemButtons;
    private final ButtonGroup<Button> itemButtonGroup;
    private int currentItem;

    private PlayerEntity playerEntity;

    public InventoryView(Skin skin) {
        effectButtons = new InventoryButton[MAX_SLOTS];
        itemButtons = new InventoryButton[MAX_SLOTS];

        this.playerEntity = null;

        init(skin);

        itemButtonGroup = new ButtonGroup<>();
        for (int i = 0; i < MAX_SLOTS; ++i) {
            itemButtonGroup.add(itemButtons[i].getButton());
        }
        itemButtonGroup.setMaxCheckCount(1);
        itemButtonGroup.setMinCheckCount(0);
        itemButtonGroup.setUncheckLast(true);

        currentItem = -1;
    }

    public void init(Skin skin) {
        Label inventoryLabel = new Label("Inventory", skin);
        Table effects = new Table();
        Table items = new Table();
        NinePatch separator = new NinePatch(Atlas.getSkinAtlas().findRegion("separator"));

        for (int i = 0; i < MAX_SLOTS; ++i) {
            effectButtons[i] = new InventoryButton(null, skin);
            itemButtons[i] = new InventoryButton(null, skin);

            float pad = PPM / 4;
            if (i == MAX_SLOTS - 1) {
                pad = 0;
            }

            effects.add(effectButtons[i].getButton()).padBottom(pad).row();
            items.add(itemButtons[i].getButton()).padBottom(pad).row();
        }

        add(inventoryLabel)
            .colspan(3)
            .padBottom(PPM / 2)
            .row();
        add(effects).expandX();
        add(new Image(separator))
            .expandY()
            .fillY()
            .minWidth(2)
            .prefWidth(2);
        add(items).expandX();
    }

    public void setPlayerEntity(PlayerEntity entity) {
        playerEntity = entity;
    }

    private boolean scanForInventoryChanges() {
        boolean change = false;

        // Effects
        for (ItemStack stack : playerEntity.getInventory().getUpgradeItems()) {
            final Optional<InventoryButton> correspondingButton = Arrays.stream(effectButtons)
                .filter(btn -> btn.type == stack.getItemType())
                .findFirst();

            if (correspondingButton.isPresent()) {
                correspondingButton.get().setQuantity(stack.getQuantity());
            } else {
                final int slot = getNextEmptyEffectSlot();
                if (slot != -1) {
                    effectButtons[slot].type = stack.getItemType();
                    effectButtons[slot].updateDrawable();
                    effectButtons[slot].setQuantity(stack.getQuantity());
                    change = true;
                }
            }
        }

        // Items
        for (ItemStack stack : playerEntity.getInventory().getUsableItems()) {
            final Optional<InventoryButton> correspondingButton = Arrays.stream(itemButtons)
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

        // Clean empty stacks
        for (InventoryButton button : itemButtons) {
            if (button.isEmpty()) {
                continue;
            }

            if (playerEntity.getInventory().getUsableItems().stream()
                .noneMatch(it -> button.type == it.getItemType())) {
                changeCurrentItem(true);
                button.makeEmpty();
                button.updateDrawable();
                change = true;
            }
        }

        return change;
    }

    private void rearrangeButtons() {
        final List<ImmutablePair<ItemType, Integer>> partitionedEffects = Arrays.stream(effectButtons)
            .collect(Collectors.partitioningBy(InventoryButton::isEmpty))
            .values().stream()
            .flatMap(List::stream)
            .map(btn -> new ImmutablePair<>(btn.type, btn.getQuantity()))
            .collect(Collectors.toList());

        final List<ImmutablePair<ItemType, Integer>> partitionedItems = Arrays.stream(itemButtons)
            .collect(Collectors.partitioningBy(InventoryButton::isEmpty))
            .values().stream()
            .flatMap(List::stream)
            .map(btn -> new ImmutablePair<>(btn.type, btn.getQuantity()))
            .collect(Collectors.toList());

        for (int i = 0; i < MAX_SLOTS; ++i) {
            effectButtons[i].type = partitionedEffects.get(i).getKey();
            effectButtons[i].updateDrawable();
            effectButtons[i].setQuantity(partitionedEffects.get(i).getValue());

            itemButtons[i].type = partitionedItems.get(i).getKey();
            itemButtons[i].setQuantity(partitionedItems.get(i).getValue());
            itemButtons[i].updateDrawable();
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

    public ItemType getCurrentItem() {
        return currentItem == -1 ? null : itemButtons[currentItem].type;
    }

    public void changeCurrentItem(boolean up) {
        final int itemCount = (int) Arrays.stream(itemButtons)
            .filter(btn -> btn.type != null)
            .count();

        if (itemCount == 0) {
            return;
        }

        if (currentItem == -1) {
            currentItem = 0;
        } else if (up && currentItem == 0) {
            currentItem = itemCount - 1;
        } else {
            final int direction = up ? -1 : 1;
            currentItem = (currentItem + direction) % itemCount;
        }

        // Check current item
        itemButtons[currentItem].getButton().setChecked(true);
    }

    private void updateCurrentItem() {
        if(currentItem != -1 && itemButtons[currentItem].getButton().isChecked()) {
            return;
        }

        int updatedCurrentItem = -1;

        for (int i = 0; i < MAX_SLOTS; ++i) {
            if (itemButtons[i].getButton().isChecked()) {
                if (itemButtons[i].isEmpty()) {
                    itemButtons[i].getButton().setChecked(false);
                } else {
                    updatedCurrentItem = i;
                }
            }
        }

        currentItem = updatedCurrentItem;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (playerEntity != null && scanForInventoryChanges())
            rearrangeButtons();

        updateCurrentItem();
    }
}
