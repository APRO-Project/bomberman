package com.cyberbot.bomberman.net;

import com.cyberbot.bomberman.core.models.net.Connection;
import com.cyberbot.bomberman.core.models.snapshots.GameSnapshot;
import com.cyberbot.bomberman.core.models.snapshots.PlayerSnapshot;
import com.cyberbot.bomberman.core.utils.Utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class NetService implements Runnable {
    private DatagramSocket socket;
    private final InetAddress address;
    private final int port;
    private final GameSnapshotListener listener;

    public NetService(Connection connection, GameSnapshotListener listener) {
        this(connection.getAddress(), connection.getPort(), listener);
    }

    public NetService(InetAddress address, int port, GameSnapshotListener listener) {
        this.address = address;
        this.port = port;
        this.listener = listener;
    }

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
        try {
            socket = new DatagramSocket();
            byte[] buffer = new byte[2048];
            for (; ; ) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                GameSnapshot snapshot = Utils.fromByteArray(buffer, GameSnapshot.class);
                listener.onNewSnapshot(snapshot);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void sendPlayerSnapshot(PlayerSnapshot snapshot) {
        byte[] buf = Utils.toByteArray(snapshot);
        if (buf == null) {
            throw new RuntimeException("Cannot serialize snapshot");
        }
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
