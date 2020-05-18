package com.cyberbot.bomberman.controllers;

import com.badlogic.gdx.Gdx;
import com.cyberbot.bomberman.core.controllers.ActionListener;
import com.cyberbot.bomberman.core.controllers.MovementListener;
import com.cyberbot.bomberman.core.controllers.PlayerMovementController;
import com.cyberbot.bomberman.core.models.Updatable;
import com.cyberbot.bomberman.core.models.actions.UseItemAction;
import com.cyberbot.bomberman.core.models.items.ItemType;
import com.cyberbot.bomberman.models.KeyBinds;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles all input from the user and forwards the actions to the action controller.
 */
public final class InputController implements Updatable {
    private final KeyBinds keys;
    private final List<ActionListener> actionListeners;
    private final List<MovementListener> movementListeners;

    public InputController(KeyBinds keys) {
        this.keys = keys;
        this.actionListeners = new ArrayList<>();
        this.movementListeners = new ArrayList<>();
    }

    public void addActionController(ActionListener controller) {
        actionListeners.add(controller);
    }

    public void addMovementController(MovementListener controller) {
        movementListeners.add(controller);
    }

    @Override
    public void update(float delta) {
        handleMove();
        handleItem();
    }

    private void handleItem() {
        if (Gdx.input.isKeyJustPressed(keys.useItem)) {
            // TODO: Get selected item from HUD
            actionListeners.forEach(c -> c.executeAction(new UseItemAction(ItemType.SMALL_BOMB)));
        }
    }

    private void handleMove() {
        int direction = 0;
        if (Gdx.input.isKeyPressed(keys.up)) {
            direction |= PlayerMovementController.MOVE_UP;
        }
        if (Gdx.input.isKeyPressed(keys.down)) {
            direction |= PlayerMovementController.MOVE_DOWN;
        }
        if (Gdx.input.isKeyPressed(keys.right)) {
            direction |= PlayerMovementController.MOVE_RIGHT;
        }
        if (Gdx.input.isKeyPressed(keys.left)) {
            direction |= PlayerMovementController.MOVE_LEFT;
        }

        final int finalDirection = direction;
        movementListeners.forEach(c -> c.move(finalDirection));
    }
}
