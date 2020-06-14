package com.cyberbot.bomberman.core.models.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.cyberbot.bomberman.core.models.defs.PlayerDef;
import com.cyberbot.bomberman.core.models.items.Inventory;
import com.cyberbot.bomberman.core.models.net.data.EntityData;
import com.cyberbot.bomberman.core.models.net.data.PlayerData;
import com.cyberbot.bomberman.core.models.tiles.FloorTile;
import com.cyberbot.bomberman.core.models.tiles.Tile;
import com.cyberbot.bomberman.core.models.tiles.TileMap;

import static com.cyberbot.bomberman.core.utils.Constants.PPM;

public class PlayerEntity extends Entity {
    public static final float MAX_VELOCITY = 5;
    public static final float MAX_VELOCITY_RAW = MAX_VELOCITY * PPM;
    public static final float DRAG_BASE = 60f;
    public static final int BOX2D_GROUP_INDEX = -1;

    private Fixture fixture;
    private Inventory inventory;
    private final int textureVariant;

    private float dragMultiplier;
    private float maxSpeedMultiplier;

    public enum FacingDirection {BACK, FRONT, LEFT, RIGHT}

    public FacingDirection facingDirection;

    private int hp;

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        if (hp < 0 || hp > 100) {
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

        inventory = def.inventory;
        dragMultiplier = def.dragModifier;
        maxSpeedMultiplier = def.maxSpeedModifier;
        textureVariant = def.textureVariant;
        hp = def.hp;
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

        Filter playerFilter = new Filter();
        playerFilter.groupIndex = BOX2D_GROUP_INDEX;
        fixture = body.createFixture(shape, 1);
        fixture.setFilterData(playerFilter);
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
        return new PlayerData(id, getPosition(), inventory, textureVariant, facingDirection, hp);
    }

    @Override
    public void updateFromData(EntityData<?> d0, EntityData<?> d1, float fraction) {
        if (!(d0 instanceof PlayerData) || !(d1 instanceof PlayerData)) {
            throw new IllegalArgumentException("Not instance of PlayerData");
        }

        super.updateFromData(d0, d1, fraction);
        facingDirection = ((PlayerData) d0).getFacingDirection();
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

    public boolean isAlive() {
        return hp > 0;
    }

}
