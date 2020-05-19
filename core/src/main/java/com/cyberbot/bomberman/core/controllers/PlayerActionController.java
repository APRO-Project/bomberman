package com.cyberbot.bomberman.core.controllers;

import com.cyberbot.bomberman.core.models.actions.Action;
import com.cyberbot.bomberman.core.models.actions.UseItemAction;
import com.cyberbot.bomberman.core.models.defs.BombDef;
import com.cyberbot.bomberman.core.models.entities.PlayerEntity;
import com.cyberbot.bomberman.core.models.items.ItemType;

import java.util.ArrayList;
import java.util.List;

public final class PlayerActionController implements ActionListener {
    private final PlayerEntity player;
    private final List<Listener> listeners;

    public PlayerActionController(PlayerEntity playerEntity) {
        this.player = playerEntity;
        this.listeners = new ArrayList<>();
    }


    public void addListener(PlayerActionController.Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(PlayerActionController.Listener listener) {
        listeners.remove(listener);
    }

    public void clearListeners() {
        listeners.clear();
    }

    @Override
    public void executeAction(Action action) {
        if (action instanceof UseItemAction) {
            useItem(((UseItemAction) action).getItemType());
        }
    }

    private void useItem(ItemType itemType) {
        if (!player.getInventory().removeItem(itemType)) {
            return;
        }

        switch (itemType) {
            case SMALL_BOMB:
                // TODO: Load a proper texture variant
                BombDef def = new BombDef(2, 5, 3, player.getTextureVariant());
                listeners.forEach(l -> l.onBombPlaced(def, player));
        }
    }

    public interface Listener {
        void onBombPlaced(BombDef bombDef, PlayerEntity executor);
    }
}
