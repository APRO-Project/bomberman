package com.cyberbot.bomberman.models.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import com.cyberbot.bomberman.screens.GameScreen;

import static com.cyberbot.bomberman.utils.Constants.PPM;

public class PlayerEntity extends Entity {
    private static final float ANIMATION_DURATION = 0.2f;

    private final TextureRegion standingBack;
    private final TextureRegion standingFront;
    private final TextureRegion standingSide;

    private final Animation<TextureRegion> movingFront;
    private final Animation<TextureRegion> movingBack;
    private final Animation<TextureRegion> movingSide;
    private final TextureAtlas atlas;

    private PlayerState currentState;
    private PlayerState previousState;

    private LookingDirection verticalDirection;
    private LookingDirection horizontalDirection;

    private float stateTimer;
    private GameScreen screen;
    private World world;

    // TODO: Create PlayerDef to hold these and Inventory
    private float dragModifier;
    private float maxSpeedModifier;

    public PlayerEntity(World world, String atlasPath) {
        super(world);

        atlas = new TextureAtlas("textures/bomberman_player.atlas");

        currentState = PlayerState.STANDING;
        previousState = PlayerState.STANDING;
        verticalDirection = LookingDirection.RIGHT;
        horizontalDirection = null;
        stateTimer = 0;

        standingBack = atlas.findRegion("standing_back");
        standingFront = atlas.findRegion("standing_front");
        standingSide = atlas.findRegion("standing_side");

        movingFront = new Animation<>(ANIMATION_DURATION, atlas.findRegions("moving_front"));
        movingBack = new Animation<>(ANIMATION_DURATION, atlas.findRegions("moving_back"));
        movingSide = new Animation<>(ANIMATION_DURATION, atlas.findRegions("moving_side"));

        sprite = new Sprite(standingSide);
    }

    public float getDragModifier() {
        return dragModifier;
    }

    public void setDragModifier(float dragModifier) {
        this.dragModifier = dragModifier;
    }

    public float getMaxSpeedModifier() {
        return maxSpeedModifier;
    }

    public void setMaxSpeedModifier(float maxSpeedModifier) {
        this.maxSpeedModifier = maxSpeedModifier;
    }

    public void setGameScreenAndWorld(GameScreen gameScreen, World world) {
        screen = gameScreen;
        this.world = world;
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        sprite.setRegion(getFrame(delta));
    }

    private TextureRegion getFrame(float delta) {
        TextureRegion frameRegion;

        currentState = getState();
        switch (currentState) {
            case MOVING_BACK:
                frameRegion = movingBack.getKeyFrame(stateTimer, true);
                break;
            case MOVING_FRONT:
                frameRegion = movingFront.getKeyFrame(stateTimer, true);
                break;
            case MOVING_SIDE:
                frameRegion = movingSide.getKeyFrame(stateTimer, true);
                break;
            case STANDING:
            default:
                if (verticalDirection == null) {
                    if (horizontalDirection == LookingDirection.UP) frameRegion = standingBack;
                    else frameRegion = standingFront;
                } else frameRegion = standingSide;
        }

        if (verticalDirection == LookingDirection.LEFT && !frameRegion.isFlipX())
            frameRegion.flip(true, false);
        else if (verticalDirection == LookingDirection.RIGHT && frameRegion.isFlipX())
            frameRegion.flip(true, false);

        stateTimer = currentState == previousState ? stateTimer + delta : 0;
        previousState = currentState;

        return frameRegion;
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
    public void dispose() {
        super.dispose();
        atlas.dispose();
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
