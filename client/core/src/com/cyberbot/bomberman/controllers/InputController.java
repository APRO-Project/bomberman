package com.cyberbot.bomberman.controllers;

import com.badlogic.gdx.Gdx;
import com.cyberbot.bomberman.core.controllers.ActionController;
import com.cyberbot.bomberman.core.models.items.ItemType;
import com.cyberbot.bomberman.models.KeyBinds;
import com.cyberbot.bomberman.screens.hud.GameHud;

/**
 * Handles all input from the user and forwards the actions to the action controller.
 */
public final class InputController {
    private final KeyBinds keys;
    private final ActionController actionController;
    private final GameHud hud;

    public InputController(KeyBinds keys, ActionController actionController, GameHud hud) {
        this.keys = keys;
        this.actionController = actionController;
        this.hud = hud;
    }

    public void update() {
        handleMove();
        handleItem();
    }

    private void handleItem() {
        if (Gdx.input.isKeyJustPressed(keys.useItem)) {
            final ItemType item = hud.getCurrentItem();
            if (item != null) {
                actionController.useItem(item);
            }
        }
    }

    private void handleMove() {
        int playerDirection = 0;
        if (Gdx.input.isKeyPressed(keys.up)) {
            playerDirection |= ActionController.MOVE_UP;
        }
        if (Gdx.input.isKeyPressed(keys.down)) {
            playerDirection |= ActionController.MOVE_DOWN;
        }
        if (Gdx.input.isKeyPressed(keys.right)) {
            playerDirection |= ActionController.MOVE_RIGHT;
        }
        if (Gdx.input.isKeyPressed(keys.left)) {
            playerDirection |= ActionController.MOVE_LEFT;
        }

        actionController.move(playerDirection);
    }
}
