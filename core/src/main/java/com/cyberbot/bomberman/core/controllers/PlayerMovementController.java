package com.cyberbot.bomberman.core.controllers;

import com.badlogic.gdx.math.Vector2;
import com.cyberbot.bomberman.core.models.entities.PlayerEntity;

import static com.cyberbot.bomberman.core.utils.Constants.PPM;

public class PlayerMovementController implements MovementListener {
    public static final int MOVE_LEFT = 0x01;
    public static final int MOVE_RIGHT = 0x02;
    public static final int MOVE_UP = 0x04;
    public static final int MOVE_DOWN = 0x08;

    private static final float MAX_VELOCITY_BASE = 5 * PPM;
    private static final float DRAG_BASE = 60f;

    protected final PlayerEntity playerEntity;

    public PlayerMovementController(PlayerEntity playerEntity) {
        this.playerEntity = playerEntity;
    }

    public void move(int direction) {
        float maxVelocity = MAX_VELOCITY_BASE * playerEntity.getMaxSpeedModifier();
        float drag = DRAG_BASE * playerEntity.getDragModifier();

        Vector2 velocity = playerEntity.getVelocity();
        if (direction == 0 && velocity.len() < 1) {
            playerEntity.setVelocity(new Vector2(0, 0));
        }

        float desiredVelocityX = 0;
        float desiredVelocityY = 0;

        if ((direction & MOVE_LEFT) > 0) {
            desiredVelocityX -= maxVelocity;
        }
        if ((direction & MOVE_RIGHT) > 0) {
            desiredVelocityX += maxVelocity;
        }
        if ((direction & MOVE_UP) > 0) {
            desiredVelocityY += maxVelocity;
        }
        if ((direction & MOVE_DOWN) > 0) {
            desiredVelocityY -= maxVelocity;
        }

        float velocityChangeX = desiredVelocityX - velocity.x;
        float velocityChangeY = desiredVelocityY - velocity.y;

        float forceX = playerEntity.getMass() * velocityChangeX * drag;
        float forceY = playerEntity.getMass() * velocityChangeY * drag;

        Vector2 force = new Vector2(forceX, forceY);

        playerEntity.applyForce(force);
    }
}
