package com.cyberbot.bomberman.models;

import com.badlogic.gdx.Input;

@SuppressWarnings("CanBeFinal")
public class KeyBinds {
    public int up = Input.Keys.W;
    public int down = Input.Keys.S;
    public int left = Input.Keys.A;
    public int right = Input.Keys.D;
    public int useItem = Input.Keys.SPACE;

    // Inventory view
    public int switchItemUp = Input.Keys.Q;
    public int switchItemDown = Input.Keys.E;
}
