package com.cyberbot.bomberman.core.models.net;

import com.badlogic.gdx.math.Vector2;

import java.io.Serializable;

public class VectorData implements Serializable {
    public final float x;
    public final float y;

    public VectorData(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2 toVector2() {
        return new Vector2(x, y);
    }
}
