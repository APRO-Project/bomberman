package com.cyberbot.bomberman;

import com.badlogic.gdx.ApplicationListener;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Application implements ApplicationListener {
    @Override
    public void create() {
        ServerService service = new ServerService(8038);
        new Thread(service).start();

        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);
        executorService.scheduleAtFixedRate(service::tick, 0, 1_000 / 20, TimeUnit.MILLISECONDS);
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
