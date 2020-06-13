package com.cyberbot.bomberman.sprites;

import com.badlogic.gdx.math.Vector2;
import com.cyberbot.bomberman.core.models.entities.CollectibleEntity;

import static com.cyberbot.bomberman.core.utils.Constants.ANIMATION_SPEED;

public class CollectibleSprite extends EntitySprite<CollectibleEntity> {
    private static final float ANIMATION_DURATION = 1f;
    private static final float ANIMATION_OFFSET = 0.1f;

    private float animationStage;
    private int animationDirection;

    public CollectibleSprite(CollectibleEntity entity) {
        super(entity);
        sprite.set(GraphicsFactory.createSprite(entity));
        sprite.setScale(0.75f);

        animationStage = 0f;
        animationDirection = 1;
    }

    @Override
    public void update(float delta) {
        animateBobbing(delta);
    }

    public void animateBobbing(float delta) {
        animationStage = (animationStage + animationDirection * (delta * ANIMATION_SPEED / ANIMATION_DURATION));
        if (animationStage > 1) {
            animationStage = 2 - animationStage;
            animationDirection = -1;
        } else if (animationStage < 0) {
            animationStage = -animationStage;
            animationDirection = 1;
        }

        Vector2 position = entity.getPositionRaw();
        float x = position.x - sprite.getWidth() / 2;
        float y = position.y - sprite.getHeight() / 2;

        float dy = (animationStage - 0.5f) * sprite.getHeight() * ANIMATION_OFFSET;
        y += dy;
        sprite.setPosition(x, y);
    }
}
