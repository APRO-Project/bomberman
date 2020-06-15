package com.cyberbot.bomberman.screens.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.IntAction;
import com.cyberbot.bomberman.core.models.entities.PlayerEntity;
import com.cyberbot.bomberman.utils.Atlas;

import java.util.LinkedList;

public class HealthBar extends Actor {

    private PlayerEntity playerEntity;

    private final NinePatch progressBarEmpty;
    private final NinePatch progressBarFill;

    private int visiblePlayerHealth;

    private static final float HP_PER_SECOND = 100;
    private static final int HEALTH_MEDIUM = 40;
    private static final int HEALTH_LOW = 10;

    private final LinkedList<IntAction> animationQueue;
    private IntAction currentAnimation;

    public HealthBar(float width, float height) {
        super();

        this.playerEntity = null;
        visiblePlayerHealth = 100;

        // Load textures
        progressBarEmpty = new NinePatch(Atlas.getSkinAtlas().findRegion("progress_bar_empty"));
        progressBarFill = new NinePatch(Atlas.getSkinAtlas().findRegion("progress_bar_fill"));
        setBounds(0,0, width, height);

        animationQueue = new LinkedList<>();
        currentAnimation = null;
    }

    private void updatePlayerHealth() {
        if(playerEntity != null) {
            addHealthAnimation(actualCurrentHealth(), playerEntity.getHp());
        }
    }

    private void addHealthAnimation(int start, int end) {
        if(start != end) {
            animationQueue.add(new IntAction(start, end, Math.abs(end - start) / HP_PER_SECOND, Interpolation.sineOut));
        }
    }

    private int actualCurrentHealth() {
        return animationQueue.isEmpty() ? visiblePlayerHealth : animationQueue.getLast().getEnd();
    }

    public void setPlayerEntity(PlayerEntity entity) {
        playerEntity = entity;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        updatePlayerHealth();

        if(currentAnimation == null) {
            if(!animationQueue.isEmpty()) {
                currentAnimation = animationQueue.getFirst();
                addAction(currentAnimation);
            }
        }
        else {
            visiblePlayerHealth = currentAnimation.getValue();
            if(currentAnimation.isComplete()) {
                animationQueue.removeFirst();
                currentAnimation = null;
            }
        }

        if(visiblePlayerHealth > HEALTH_MEDIUM) progressBarFill.setColor(Color.GREEN);
        else if(visiblePlayerHealth > HEALTH_LOW) progressBarFill.setColor(Color.ORANGE);
        else progressBarFill.setColor(Color.RED);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        progressBarEmpty.draw(batch, getX(), getY(), getWidth(), getHeight());
        progressBarFill.draw(batch, getX(), getY(), visiblePlayerHealth * getWidth() / 100, getHeight());
    }
}
