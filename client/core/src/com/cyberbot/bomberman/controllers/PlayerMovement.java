package com.cyberbot.bomberman.controllers;

import com.badlogic.gdx.math.Vector2;
import com.cyberbot.bomberman.models.entities.PlayerEntity;

import static com.cyberbot.bomberman.utils.Constants.PPM;

public class PlayerMovement {
    public static final int LEFT = 0x01;
    public static final int RIGHT = 0x02;
    public static final int UP = 0x04;
    public static final int DOWN = 0x08;

    private static final float MAX_VELOCITY_BASE = 5 * PPM;
    private static final float DRAG_BASE = 60f;

    private final PlayerEntity playerEntity;

    public PlayerMovement(PlayerEntity playerEntity) {
        this.playerEntity = playerEntity;
    }

    public void move(int direction) {
        float maxVelocity  = MAX_VELOCITY_BASE * playerEntity.getMaxSpeedModifier();
        float drag  = DRAG_BASE * playerEntity.getDragModifier();

        Vector2 velocity = playerEntity.getVelocity();
        if(direction == 0 && velocity.len() < 1) {
            playerEntity.setVelocity(new Vector2(0,0));
        }

        float desiredVelocityX = 0;
        float desiredVelocityY = 0;

        if ((direction & LEFT) > 0) {
            desiredVelocityX -= maxVelocity;
        }
        if ((direction & RIGHT) > 0) {
            desiredVelocityX += maxVelocity;
        }
        if ((direction & UP) > 0) {
            desiredVelocityY += maxVelocity;
        }
        if ((direction & DOWN) > 0) {
            desiredVelocityY -= maxVelocity;
        }

        float velocityChangeX = desiredVelocityX - velocity.x;
        float velocityChangeY = desiredVelocityY - velocity.y;

        float forceX = playerEntity.getMass() * velocityChangeX * drag;
        float forceY = playerEntity.getMass() * velocityChangeY * drag;

        Vector2 force = new Vector2(forceX,forceY);

        playerEntity.applyForce(force);
    }

    public PlayerEntity getPlayerEntity() {
        return playerEntity;
    }
}
