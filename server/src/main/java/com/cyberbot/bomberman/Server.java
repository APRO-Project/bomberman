package com.cyberbot.bomberman;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Server {
    public static void main(String[] args) {
        ServerService service = new ServerService(8038);
        new Thread(service).start();

        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);
        executorService.scheduleAtFixedRate(service::tick, 0, 50, TimeUnit.MILLISECONDS);
    }
}
