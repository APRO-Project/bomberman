package com.cyberbot.bomberman.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.cyberbot.bomberman.models.KeyBinds;
import com.cyberbot.bomberman.models.entities.PlayerEntity;

public class InputController {
    private KeyBinds keys;
    private PlayerMovement movementController;

    public InputController(KeyBinds keys, PlayerMovement movementController) {
        this.keys = keys;
        this.movementController = movementController;
    }

    public void update() {
        int playerDirection = 0;
        if(Gdx.input.isKeyPressed(keys.up)) {
            playerDirection |= PlayerMovement.UP;
            movementController.getPlayerEntity().setLookingDirection(PlayerEntity.LookingDirection.UP);
        }
        if(Gdx.input.isKeyPressed(keys.down)) {
            playerDirection |= PlayerMovement.DOWN;
            movementController.getPlayerEntity().setLookingDirection(PlayerEntity.LookingDirection.DOWN);
        }
        if(Gdx.input.isKeyPressed(keys.right)) {
            playerDirection |= PlayerMovement.RIGHT;
            movementController.getPlayerEntity().setLookingDirection(PlayerEntity.LookingDirection.RIGHT);
        }
        if(Gdx.input.isKeyPressed(keys.left)) {
            playerDirection |= PlayerMovement.LEFT;
            movementController.getPlayerEntity().setLookingDirection(PlayerEntity.LookingDirection.LEFT);
        }

        movementController.move(playerDirection);
    }
}
