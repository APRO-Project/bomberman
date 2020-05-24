package com.cyberbot.bomberman.server;

import com.cyberbot.bomberman.core.models.net.InvalidPacketFormatException;
import com.cyberbot.bomberman.core.models.net.SerializationUtils;
import com.cyberbot.bomberman.core.models.net.packets.PlayerSnapshotPacket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

public class ServerService implements Runnable {
    private final int port;
    private DatagramSocket socket;
    private List<Session> sessions = new ArrayList<>(1);

    public ServerService(int port) {
        this.port = port;
        sessions.add(new Session(this));
    }

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
        try {
            socket = new DatagramSocket(port);
            for (; ; ) {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                ClientConnection client = ClientConnection.fromDatagramPacket(packet);

                Object o = SerializationUtils.INSTANCE.deserialize(buffer, packet.getLength(), 0);

                if (o instanceof PlayerSnapshotPacket) {
                    sessions.stream()
                        .filter(it -> it.hasClient(client))
                        .findFirst()
                        .ifPresent(session -> session.onSnapshot(client, (PlayerSnapshotPacket) o));
                }
            }
        } catch (IOException | InvalidPacketFormatException e) {
            e.printStackTrace();
        }
    }

    public void send(DatagramPacket packet) throws IOException {
        socket.send(packet);
    }

    private void send(ClientConnection client, byte[] data, int length) throws IOException {
        DatagramPacket packet = new DatagramPacket(data, 0, length, client.getAddress(), client.getPort());
        socket.send(packet);
    }
}
