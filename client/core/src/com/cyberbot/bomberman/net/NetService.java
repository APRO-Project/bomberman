package com.cyberbot.bomberman.net;

import com.cyberbot.bomberman.core.models.net.GameSnapshotListener;
import com.cyberbot.bomberman.core.models.net.InvalidPacketFormatException;
import com.cyberbot.bomberman.core.models.net.SerializationUtils;
import com.cyberbot.bomberman.core.models.net.packets.GameSnapshotPacket;
import com.cyberbot.bomberman.core.models.net.packets.PlayerSnapshotPacket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

public class NetService implements Runnable {
    private DatagramSocket socket;
    private final SocketAddress address;
    private final GameSnapshotListener listener;

    public NetService(SocketAddress address, GameSnapshotListener listener) {
        this.address = address;
        this.listener = listener;
    }

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
        try {
            socket = new DatagramSocket();
            socket.connect(address);
            byte[] buffer = new byte[2048];
            for (; ; ) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                Object o = SerializationUtils.INSTANCE.deserialize(buffer, packet.getLength(), 0);
                if (o instanceof GameSnapshotPacket) {
                    listener.onNewSnapshot((GameSnapshotPacket) o);
                }
            }
        } catch (IOException | InvalidPacketFormatException e) {
            e.printStackTrace();
        }
    }

    public synchronized void sendPlayerSnapshot(PlayerSnapshotPacket packet) {
        byte[] buf = SerializationUtils.INSTANCE.serialize(packet);
        DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length, address);
        try {
            socket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
