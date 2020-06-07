package com.cyberbot.bomberman.core.models.net.data;

import com.badlogic.gdx.math.Vector2;

import java.io.Serializable;

public class VectorData implements Serializable {
    public float x;
    public float y;

    public VectorData(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void add(Vector2 vector2) {
        x += vector2.x;
        y += vector2.y;
    }

    public Vector2 toVector2() {
        return new Vector2(x, y);
    }
}
