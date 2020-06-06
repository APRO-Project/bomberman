package com.cyberbot.bomberman.core.models.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.cyberbot.bomberman.core.models.defs.PlayerDef;
import com.cyberbot.bomberman.core.models.items.Inventory;
import com.cyberbot.bomberman.core.models.net.data.PlayerData;

import static com.cyberbot.bomberman.core.utils.Constants.PPM;

public class PlayerEntity extends Entity {
    public static final float MAX_VELOCITY_BASE = 5 * PPM;
    public static final float DRAG_BASE = 60f;

    private static final float ANIMATION_DURATION = 0.2f;

    private Fixture fixture;
    private final int textureVariant;
    private final Inventory inventory;

    private float dragModifier;
    private float maxSpeedModifier;

    private PlayerState currentState;
    private PlayerState previousState;

    private LookingDirection verticalDirection;
    private LookingDirection horizontalDirection;


    public PlayerEntity(World world, PlayerDef def, long id) {
        super(world, id);

        currentState = PlayerState.STANDING;
        previousState = PlayerState.STANDING;
        verticalDirection = LookingDirection.RIGHT;
        horizontalDirection = null;
        inventory = def.inventory;
        dragModifier = def.dragModifier;
        maxSpeedModifier = def.maxSpeedModifier;
        textureVariant = def.textureVariant;
    }

    public float getDragModifier() {
        return dragModifier;
    }

    public void setDragModifier(float dragModifier) {
        this.dragModifier = dragModifier;
    }

    public float getMaxSpeedModifier() {
        return maxSpeedModifier * inventory.getMovementSpeedMultiplier();
    }

    public void setMaxSpeedModifier(float maxSpeedModifier) {
        this.maxSpeedModifier = maxSpeedModifier;
    }

    public int getTextureVariant() {
        return textureVariant;
    }

    public Inventory getInventory() {
        return inventory;
    }

    private PlayerState getState() {
        if (getVelocity().x == 0 && getVelocity().y == 0)
            return PlayerState.STANDING;

        if (getVelocity().x != 0 && verticalDirection != null)
            return PlayerState.MOVING_SIDE;

        if (horizontalDirection == LookingDirection.UP)
            return PlayerState.MOVING_BACK;
        else
            return PlayerState.MOVING_FRONT;
    }

    public void setLookingDirection(LookingDirection direction) {
        if (direction == LookingDirection.UP || direction == LookingDirection.DOWN) {
            horizontalDirection = direction;
            verticalDirection = null;
        } else {
            horizontalDirection = null;
            verticalDirection = direction;
        }
    }

    public void applyForce(Vector2 force) {
        body.applyForceToCenter(force.x / PPM, force.y / PPM, true);
    }

    public Vector2 getVelocity() {
        Vector2 velocity = body.getLinearVelocity();
        return new Vector2(velocity.x * PPM, velocity.y * PPM);
    }

    public Vector2 getVelocityRaw() {
        return new Vector2(body.getLinearVelocity());
    }

    public void setVelocity(Vector2 velocity) {
        body.setLinearVelocity(velocity.x / PPM, velocity.y / PPM);
    }

    public void setVelocityRaw(Vector2 velocity) {
        body.setLinearVelocity(velocity);
    }

    public float getMass() {
        return body.getMass();
    }

    public void setCollisions(boolean enabled) {
        fixture.setSensor(!enabled);
    }

    @Override
    public void createBody(World world) {
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set(0, 0);
        def.fixedRotation = true;

        body = world.createBody(def);

        CircleShape shape = new CircleShape();
        shape.setRadius(0.49f);

        fixture = body.createFixture(shape, 1);
        body.setUserData(this);
        shape.dispose();
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        inventory.update(delta);
    }

    @Override
    public PlayerData getData() {
        return new PlayerData(id, getPositionRaw(), inventory, textureVariant);
    }

    public enum PlayerState {
        STANDING,
        MOVING_BACK,
        MOVING_FRONT,
        MOVING_SIDE
    }

    public enum LookingDirection {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }
}
