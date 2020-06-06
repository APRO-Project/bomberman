package com.cyberbot.bomberman.screens.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.IntAction;
import com.cyberbot.bomberman.core.models.net.data.PlayerData;
import com.cyberbot.bomberman.utils.Atlas;

import java.util.LinkedList;

public class HealthBar extends Actor {

    private final PlayerData playerData;

    private final NinePatch progressBarEmpty;
    private final NinePatch progressBarFill;

    private int visiblePlayerHealth;
    private final float HP_PER_SECOND = 100;

    private final int HEALTH_MEDIUM = 40;
    private final int HEALTH_LOW = 10;

    private final LinkedList<IntAction> animationQueue;
    private IntAction currentAnimation;

    public HealthBar(PlayerData playerData, float width, float height) {
        super();

        this.playerData = playerData;
        visiblePlayerHealth = 100;  // TODO: Add health property to PlayerData

        // Load textures
        progressBarEmpty = new NinePatch(Atlas.getSkinAtlas().findRegion("progress_bar_empty"));
        progressBarFill = new NinePatch(Atlas.getSkinAtlas().findRegion("progress_bar_fill"));
        setBounds(0,0, width, height);

        animationQueue = new LinkedList<>();
        currentAnimation = null;
    }

    public void updatePlayerHealth(int playerHealth) {
        if(playerHealth < 0 || playerHealth > 100) {
            throw new IllegalArgumentException("Player health must be between 0 and 100");
        }

        addHealthAnimation(actualCurrentHealth(), playerHealth);
    }

    public void changeHealthBy(int amount) {
        int updatedHealth = actualCurrentHealth() + amount;

        // TODO: Maybe normalize the value instead of throwing an exception
        if(updatedHealth < 0 || updatedHealth > 100) {
            throw new IllegalArgumentException("Given amount causes player health to exceed acceptable range");
        }

        addHealthAnimation(actualCurrentHealth(), updatedHealth);
    }

    private void addHealthAnimation(int start, int end) {
        animationQueue.add(new IntAction(start, end, Math.abs(end - start) / HP_PER_SECOND, Interpolation.sineOut));
    }

    private int actualCurrentHealth() {
        return animationQueue.isEmpty() ? visiblePlayerHealth : animationQueue.getLast().getEnd();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

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

        // TODO: Change those colors to more customized ones
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
