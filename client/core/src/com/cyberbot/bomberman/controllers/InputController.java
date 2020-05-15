package com.cyberbot.bomberman.controllers;

import com.badlogic.gdx.Gdx;
import com.cyberbot.bomberman.models.KeyBinds;

public class InputController {
    private final KeyBinds keys;
    private final ActionController actionController;

    public InputController(KeyBinds keys, ActionController actionController) {
        this.keys = keys;
        this.actionController = actionController;
    }

    public void update() {
        handleMove();
        handleItem();
    }

    private void handleItem() {
        if (Gdx.input.isKeyJustPressed(keys.useItem)) {
            actionController.useItem(0);
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
