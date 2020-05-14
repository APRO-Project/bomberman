package com.cyberbot.bomberman.controllers;

import com.badlogic.gdx.math.Vector2;
import com.cyberbot.bomberman.models.entities.PlayerEntity;

import static com.cyberbot.bomberman.utils.Constants.PPM;

public class PlayerMovement {
    public static final int LEFT = 0x01;
    public static final int RIGHT = 0x02;
    public static final int UP = 0x04;
    public static final int DOWN = 0x08;

    private PlayerEntity playerEntity;
    private float maxVelocity = 5 * PPM;
    private float drag = 60f;

    public PlayerMovement(PlayerEntity playerEntity) {
        this.playerEntity = playerEntity;
    }

    public void move(int direction) {
        Vector2 velocity = playerEntity.getVelocity();
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
