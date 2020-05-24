package com.cyberbot.bomberman.server;

import com.badlogic.gdx.ApplicationListener;

public class Application implements ApplicationListener {
    @Override
    public void create() {
        ServerService service = new ServerService(8038);
        new Thread(service).start();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
