package com.cyberbot.bomberman.screens.hud;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.cyberbot.bomberman.core.models.items.ItemStack;
import com.cyberbot.bomberman.core.models.items.ItemType;
import com.cyberbot.bomberman.core.models.net.data.PlayerData;
import com.cyberbot.bomberman.utils.Atlas;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.cyberbot.bomberman.core.utils.Constants.PPM;

public final class InventoryView extends Table {
    // TODO: Merge InventoryButton and InventoryItemButton together

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
    private final ButtonGroup<ImageButton> itemButtonGroup;
    private int currentItem;

    private final PlayerData playerData;

    public InventoryView(PlayerData playerData, Skin skin) {
        effectButtons = new InventoryButton[5];
        itemButtons = new InventoryItemButton[5];

        this.playerData = playerData;

        init(skin);

        itemButtonGroup = new ButtonGroup<>();
        for (int i = 0; i < 5; ++i) {
            itemButtonGroup.add(itemButtons[i].button);
        }
        itemButtonGroup.setMaxCheckCount(1);
        itemButtonGroup.setMinCheckCount(0);
        itemButtonGroup.setUncheckLast(true);

        currentItem = -1;
        scanForInventoryChanges();
        changeCurrentItem(true);
    }

    public void init(Skin skin) {
        Label inventoryLabel = new Label("Inventory", skin);
        Table effects = new Table();
        Table items = new Table();
        NinePatch separator = new NinePatch(Atlas.getSkinAtlas().findRegion("separator"));

        for (int i = 0; i < 5; ++i) {
            effectButtons[i] = new InventoryButton(null);
            itemButtons[i] = new InventoryItemButton(null, skin);

            effects.add(effectButtons[i].getMainWidget()).padBottom(PPM / 2).row();
            items.add(itemButtons[i].getMainWidget()).padBottom(PPM / 2).row();
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

    private boolean scanForInventoryChanges() {
        boolean change = false;

        final ArrayList<ItemStack> effectStacks = playerData.getInventory().getItems().stream()
            .filter(i -> EFFECTS.contains(i.getItemType()))
            .collect(Collectors.toCollection(ArrayList::new));

        final ArrayList<ItemStack> itemStacks = playerData.getInventory().getItems().stream()
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
        itemButtons[currentItem].button.setChecked(true);
    }

    private void updateCurrentItem() {
        int updatedCurrentItem = -1;

        for (int i = 0; i < 5; ++i) {
            if (itemButtons[i].button.isChecked()) {
                if (itemButtons[i].isEmpty()) {
                    itemButtons[i].button.setChecked(false);
                } else {
                    updatedCurrentItem = i;
                    itemButtons[i].button.setChecked(true);
                }
            }
        }

        currentItem = updatedCurrentItem;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (scanForInventoryChanges())
            rearrangeButtons();

        updateCurrentItem();
    }
}
