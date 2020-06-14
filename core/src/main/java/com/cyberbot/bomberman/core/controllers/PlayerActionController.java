package com.cyberbot.bomberman.core.controllers;

import com.badlogic.gdx.math.Vector2;
import com.cyberbot.bomberman.core.models.Updatable;
import com.cyberbot.bomberman.core.models.actions.Action;
import com.cyberbot.bomberman.core.models.actions.MoveAction;
import com.cyberbot.bomberman.core.models.actions.UseItemAction;
import com.cyberbot.bomberman.core.models.defs.BombDef;
import com.cyberbot.bomberman.core.models.entities.PlayerEntity;
import com.cyberbot.bomberman.core.models.items.ItemType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class PlayerActionController implements ActionListener, Updatable {
    private final PlayerEntity player;
    private final List<Listener> listeners;
    private int movementDirection;

    public PlayerActionController(PlayerEntity playerEntity) {
        this.player = playerEntity;
        this.listeners = new ArrayList<>();
        this.movementDirection = 0;
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
    public void onActions(@NotNull List<Action> actions) {
        for (Action action : actions) {
            if (action instanceof UseItemAction) {
                useItem(((UseItemAction) action).getItemType());
            } else if (action instanceof MoveAction) {
                movementDirection = ((MoveAction) action).getDirection();
            }
        }
    }

    private void useItem(ItemType itemType) {
        if (!player.getInventory().removeItem(itemType)) {
            return;
        }

        switch (itemType) {
            case SMALL_BOMB: {
                BombDef def = new BombDef(10, 2, 3, itemType);
                listeners.forEach(l -> l.onBombPlaced(def, player));
                break;
            }
            case MEDIUM_BOMB: {
                BombDef def = new BombDef(25, 4, 4, itemType);
                listeners.forEach(l -> l.onBombPlaced(def, player));
                break;
            }
            case NUKE: {
                BombDef def = new BombDef(100, 10, 8, 6, itemType);
                listeners.forEach(l -> l.onBombPlaced(def, player));
                break;
            }
        }
    }

    private void move(int direction) {
        float maxVelocity = PlayerEntity.MAX_VELOCITY_RAW * player.getMaxSpeedMultiplier();
        float drag = PlayerEntity.DRAG_BASE * player.getDragMultiplier();

        Vector2 velocity = player.getVelocityRaw();
        if (direction == 0 && velocity.len() < 1) {
            player.setVelocityRaw(new Vector2(0, 0));
        }

        float desiredVelocityX = 0;
        float desiredVelocityY = 0;

        if ((direction & MoveAction.LEFT) > 0) {
            desiredVelocityX -= maxVelocity;
            player.facingDirection = PlayerEntity.FacingDirection.LEFT;
        }
        if ((direction & MoveAction.RIGHT) > 0) {
            desiredVelocityX += maxVelocity;
            player.facingDirection = PlayerEntity.FacingDirection.RIGHT;
        }
        if ((direction & MoveAction.UP) > 0) {
            desiredVelocityY += maxVelocity;
            player.facingDirection = PlayerEntity.FacingDirection.BACK;
        }
        if ((direction & MoveAction.DOWN) > 0) {
            desiredVelocityY -= maxVelocity;
            player.facingDirection = PlayerEntity.FacingDirection.FRONT;
        }

        float velocityChangeX = desiredVelocityX - velocity.x;
        float velocityChangeY = desiredVelocityY - velocity.y;

        float forceX = player.getMass() * velocityChangeX * drag;
        float forceY = player.getMass() * velocityChangeY * drag;

        Vector2 force = new Vector2(forceX, forceY);

        player.applyForce(force);
    }

    @Override
    public void update(float delta) {
        move(movementDirection);
    }

    public interface Listener {
        void onBombPlaced(BombDef bombDef, PlayerEntity executor);
    }
}
