package com.cyberbot.bomberman.screens.hud;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.cyberbot.bomberman.core.models.entities.PlayerEntity;
import com.cyberbot.bomberman.core.models.items.ItemStack;
import com.cyberbot.bomberman.core.models.items.ItemType;
import com.cyberbot.bomberman.utils.Atlas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.cyberbot.bomberman.core.utils.Constants.PPM;

public class InventoryView extends Table {

    private static final ArrayList<ItemType> ITEMS = new ArrayList<>();
    private static final ArrayList<ItemType> EFFECTS = new ArrayList<>();

    static {
        ITEMS.add(ItemType.SMALL_BOMB);

        EFFECTS.add(ItemType.UPGRADE_ARMOR);
        EFFECTS.add(ItemType.UPGRADE_MOVEMENT_SPEED);
        EFFECTS.add(ItemType.UPGRADE_REFILL_SPEED);
    }

    private final InventoryItemButton[] effectButtons;
    private final InventoryItemButton[] itemButtons;

    private final PlayerEntity player;

    public InventoryView(PlayerEntity player, Skin skin) {
        effectButtons = new InventoryItemButton[5];
        itemButtons = new InventoryItemButton[5];

        this.player = player;

        init(skin);
    }

    public void init(Skin skin) {
        Label inventoryLabel = new Label("Inventory", skin);
        Table effects = new Table();
        Table items = new Table();

        for (int i = 0; i < 5; ++i) {
            effectButtons[i] = new InventoryItemButton(null);
            itemButtons[i] = new InventoryItemButton(null);

            effects.add(effectButtons[i].button).padBottom(PPM / 2).row();
            items.add(itemButtons[i].button).padBottom(PPM / 2).row();
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

    private void scanForInventoryChanges() {
        ArrayList<ItemStack> effectStacks = player.getInventory().getItems().stream()
            .filter(i -> EFFECTS.contains(i.getItemType()))
            .collect(Collectors.toCollection(ArrayList::new));

        ArrayList<ItemStack> itemStacks = player.getInventory().getItems().stream()
            .filter(i -> ITEMS.contains(i.getItemType()))
            .collect(Collectors.toCollection(ArrayList::new));

        // Effects
        for (ItemStack stack : effectStacks) {
            Optional<InventoryItemButton> correspondingButton = Arrays.stream(effectButtons)
                .filter(btn -> btn.type == stack.getItemType())
                .findFirst();

            if (correspondingButton.isPresent()) {
                if (stack.getQuantity() == 0)
                    correspondingButton.get().makeEmpty();
                else
                    correspondingButton.get().quantity = stack.getQuantity();
            } else {
                int slot = getNextEmptyEffectSlot();
                if (slot != -1) {
                    effectButtons[slot].setType(stack.getItemType());
                    effectButtons[slot].setQuantity(stack.getQuantity());
                }
            }
        }

        // Items
        for (ItemStack stack : itemStacks) {
            Optional<InventoryItemButton> correspondingButton = Arrays.stream(itemButtons)
                .filter(btn -> btn.type == stack.getItemType())
                .findFirst();

            if (correspondingButton.isPresent()) {
                if (stack.getQuantity() == 0)
                    correspondingButton.get().makeEmpty();
                else
                    correspondingButton.get().quantity = stack.getQuantity();
            } else {
                int slot = getNextEmptyItemSlot();
                if (slot != -1) {
                    itemButtons[slot].setType(stack.getItemType());
                    itemButtons[slot].setQuantity(stack.getQuantity());
                }
            }
        }
    }

    private void sortButtons() {
        List<InventoryItemButton> partitionedEffects = Arrays.stream(effectButtons)
            .collect(Collectors.partitioningBy(InventoryItemButton::isEmpty))
            .values().stream()
            .flatMap(List::stream)
            .collect(Collectors.toList());

        List<InventoryItemButton> partitionedItems = Arrays.stream(itemButtons)
            .collect(Collectors.partitioningBy(InventoryItemButton::isEmpty))
            .values().stream()
            .flatMap(List::stream)
            .collect(Collectors.toList());

        for (int i = 0; i < effectButtons.length; ++i) {
            effectButtons[i] = partitionedEffects.get(i);
            itemButtons[i] = partitionedItems.get(i);
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

        scanForInventoryChanges();
        sortButtons();
    }
}
