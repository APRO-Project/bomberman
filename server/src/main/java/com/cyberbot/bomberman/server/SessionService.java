package com.cyberbot.bomberman.server;

import com.cyberbot.bomberman.core.models.net.InvalidPacketFormatException;
import com.cyberbot.bomberman.core.models.net.SerializationUtils;
import com.cyberbot.bomberman.core.models.net.packets.PlayerSnapshotPacket;
import com.cyberbot.bomberman.core.models.tiles.MissingLayersException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.InvalidPropertiesFormatException;

public class SessionService implements GameSocket, Runnable {
    private final int port;
    private DatagramSocket socket;
    private volatile boolean sessionActive;
    private Session session;

    public SessionService(int port) throws MissingLayersException, InvalidPropertiesFormatException {
        this.port = port;
        this.sessionActive = true;
        this.session = new Session(this);
    }

    @Override
    public void run() {
        try {
            socket = new DatagramSocket(port);
            while (sessionActive) {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                ClientConnection client = ClientConnection.fromDatagramPacket(packet);

                Object o = SerializationUtils.INSTANCE.deserialize(buffer, packet.getLength(), 0);
                if (!(o instanceof PlayerSnapshotPacket)) {
                    // TODO: Log warning
                    continue;
                }

                session.onSnapshot(client, (PlayerSnapshotPacket) o);
            }
        } catch (IOException | InvalidPacketFormatException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void send(DatagramPacket packet) throws IOException {
        socket.send(packet);
    }
}
