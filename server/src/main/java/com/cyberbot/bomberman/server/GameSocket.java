package com.cyberbot.bomberman.server;

import java.io.IOException;
import java.net.DatagramPacket;

public interface GameSocket {
    void send(DatagramPacket packet) throws IOException;
}
