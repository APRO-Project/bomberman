package com.cyberbot.bomberman.core.models.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.cyberbot.bomberman.core.models.defs.PlayerDef;
import com.cyberbot.bomberman.core.models.items.Inventory;
import com.cyberbot.bomberman.core.models.net.data.PlayerData;
import com.cyberbot.bomberman.core.models.tiles.FloorTile;
import com.cyberbot.bomberman.core.models.tiles.Tile;
import com.cyberbot.bomberman.core.models.tiles.TileMap;

import static com.cyberbot.bomberman.core.utils.Constants.PPM;

public class PlayerEntity extends Entity {
    public static final float MAX_VELOCITY = 5;
    public static final float MAX_VELOCITY_BASE = MAX_VELOCITY * PPM;
    public static final float DRAG_BASE = 60f;

    private static final float ANIMATION_DURATION = 0.2f;

    private Fixture fixture;
    private Inventory inventory;
    private final int textureVariant;

    private float dragMultiplier;
    private float maxSpeedMultiplier;

    private PlayerState currentState;
    private PlayerState previousState;

    private LookingDirection verticalDirection;
    private LookingDirection horizontalDirection;

    private int hp;

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        if(hp < 0 || hp > 100) {
            throw new IllegalArgumentException("HP value must be between 0 and 100");
        }

        this.hp = hp;
    }

    public void addHp(int value) {
        hp = Math.min(100, hp + value);
    }

    public void takeDamage(float power) {
        hp = (int) (Math.max(0, hp - power) * inventory.getArmorMultiplier());
    }

    public PlayerEntity(World world, PlayerDef def, long id) {
        super(world, id);

        currentState = PlayerState.STANDING;
        previousState = PlayerState.STANDING;
        verticalDirection = LookingDirection.RIGHT;
        horizontalDirection = null;
        inventory = def.inventory;
        dragMultiplier = def.dragModifier;
        maxSpeedMultiplier = def.maxSpeedModifier;
        textureVariant = def.textureVariant;
        hp = def.hp;
    }

    public void updateFromEnvironment(TileMap map) {
        Vector2 position = getPosition();
        int x = (int) Math.floor(position.x);
        int y = (int) Math.floor(position.y);

        Tile tile = map.getFloor().getTile(x, y);
        if (tile instanceof FloorTile) {
            FloorTile.Properties properties = ((FloorTile) tile).getProperties();
            dragMultiplier = properties.dragMultiplier;
            maxSpeedMultiplier = properties.maxSpeedMultiplier;
        } else {
            dragMultiplier = 1;
            maxSpeedMultiplier = 1;
        }
    }

    public float getDragMultiplier() {
        return dragMultiplier;
    }

    public float getMaxSpeedMultiplier() {
        return maxSpeedMultiplier * inventory.getMovementSpeedMultiplier();
    }

    public int getTextureVariant() {
        return textureVariant;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    private PlayerState getState() {
        if (getVelocityRaw().x == 0 && getVelocityRaw().y == 0)
            return PlayerState.STANDING;

        if (getVelocityRaw().x != 0 && verticalDirection != null)
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

    public Vector2 getVelocityRaw() {
        Vector2 velocity = body.getLinearVelocity();
        return new Vector2(velocity.x * PPM, velocity.y * PPM);
    }

    public void setVelocityRaw(Vector2 velocity) {
        body.setLinearVelocity(velocity.x / PPM, velocity.y / PPM);
    }

    public Vector2 getVelocity() {
        return new Vector2(body.getLinearVelocity());
    }

    public void setVelocity(Vector2 velocity) {
        body.setLinearVelocity(velocity);
    }

    public float getMass() {
        return body.getMass();
    }

    public void setCollisions(boolean enabled) {
        fixture.setSensor(!enabled);
    }

    public boolean isDead() {
        return hp == 0;
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
        return new PlayerData(id, getPosition(), inventory, textureVariant, hp);
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
