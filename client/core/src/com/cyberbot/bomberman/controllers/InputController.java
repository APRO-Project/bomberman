package com.cyberbot.bomberman.controllers;

import com.badlogic.gdx.Gdx;
import com.cyberbot.bomberman.core.controllers.ActionListener;
import com.cyberbot.bomberman.core.models.actions.Action;
import com.cyberbot.bomberman.core.models.actions.MoveAction;
import com.cyberbot.bomberman.core.models.actions.UseItemAction;
import com.cyberbot.bomberman.core.models.items.ItemType;
import com.cyberbot.bomberman.models.KeyBinds;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles all input from the user and forwards the actions to the action controller.
 */
public final class InputController {
    private final KeyBinds keys;
    private final List<ActionListener> actionListeners;

    public InputController(KeyBinds keys) {
        this.keys = keys;
        this.actionListeners = new ArrayList<>();
    }

    public void addActionController(ActionListener controller) {
        actionListeners.add(controller);
    }

    public void poll() {
        final List<Action> actions = new ArrayList<>();

        if (Gdx.input.isKeyJustPressed(keys.useItem)) {
            // TODO: Get selected item from HUD
            actions.add(new UseItemAction(ItemType.SMALL_BOMB));
        }

        int direction = 0;
        if (Gdx.input.isKeyPressed(keys.up)) {
            direction |= MoveAction.UP;
        }
        if (Gdx.input.isKeyPressed(keys.down)) {
            direction |= MoveAction.DOWN;
        }
        if (Gdx.input.isKeyPressed(keys.right)) {
            direction |= MoveAction.RIGHT;
        }
        if (Gdx.input.isKeyPressed(keys.left)) {
            direction |= MoveAction.LEFT;
        }

        actions.add(new MoveAction(direction));

        actionListeners.forEach(it -> it.onActions(actions));
    }

    private void handleItem() {

    }

    private void handleMove() {

    }
}
