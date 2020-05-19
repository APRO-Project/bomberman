package com.cyberbot.bomberman.server;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

public class Server {
    public static void main(String[] args) {
        new LwjglApplication(new Application());
    }
}
