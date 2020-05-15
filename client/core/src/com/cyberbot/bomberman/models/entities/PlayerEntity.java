package com.cyberbot.bomberman.models.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import com.cyberbot.bomberman.models.defs.PlayerDef;
import com.cyberbot.bomberman.models.items.Inventory;

import static com.cyberbot.bomberman.utils.Constants.PPM;

public class PlayerEntity extends Entity {
    private static final float ANIMATION_DURATION = 0.2f;

    private PlayerState currentState;
    private PlayerState previousState;

    private LookingDirection verticalDirection;
    private LookingDirection horizontalDirection;

    private PlayerDef def;

    public PlayerEntity(World world, PlayerDef def) {
        super(world);

        currentState = PlayerState.STANDING;
        previousState = PlayerState.STANDING;
        verticalDirection = LookingDirection.RIGHT;
        horizontalDirection = null;
        this.def = def;
    }

    public float getDragModifier() {
        return def.dragModifier;
    }

    public void setDragModifier(float dragModifier) {
        def.dragModifier = dragModifier;
    }

    public float getMaxSpeedModifier() {
        return def.maxSpeedModifier;
    }

    public void setMaxSpeedModifier(float maxSpeedModifier) {
        def.maxSpeedModifier = maxSpeedModifier;
    }

    public int getTextureVariant() {
        return def.textureVariant;
    }

    public Inventory getInventory() {
        return def.inventory;
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

    public void setVelocity(Vector2 velocity) {
        body.setLinearVelocity(velocity.x / PPM, velocity.y / PPM);
    }

    public float getMass() {
        return body.getMass();
    }

    @Override
    public void createBody(World world) {
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set(0, 0);
        def.fixedRotation = true;

        body = world.createBody(def);

        CircleShape shape = new CircleShape();
        shape.setRadius(0.49F);

        body.createFixture(shape, 1);
        body.setUserData(this);
        shape.dispose();
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        def.inventory.update(delta);
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
