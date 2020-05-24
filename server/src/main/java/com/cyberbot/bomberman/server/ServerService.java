package com.cyberbot.bomberman.server;

import com.cyberbot.bomberman.core.models.net.InvalidPacketFormatException;
import com.cyberbot.bomberman.core.models.net.SerializationUtils;
import com.cyberbot.bomberman.core.models.net.packets.LobbyCreateRequest;
import com.cyberbot.bomberman.core.models.net.packets.LobbyCreateResponse;
import com.cyberbot.bomberman.core.models.net.packets.PlayerSnapshotPacket;
import com.cyberbot.bomberman.core.utils.Utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Map;

import static com.cyberbot.bomberman.core.utils.Constants.LOBBY_ID_LENGTH;

public class ServerService implements Runnable {
    private final int port;
    private final Map<String, Session> sessions = new HashMap<>();
    private DatagramSocket socket;

    public ServerService(int port) {
        this.port = port;
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
                    sessions.values().stream()
                        .filter(it -> it.hasClient(client))
                        .findFirst()
                        .ifPresent(session -> session.onSnapshot(client, (PlayerSnapshotPacket) o));
                } else if (o instanceof LobbyCreateRequest) {
                    String name = ((LobbyCreateRequest) o).getName();
                    Session session = new Session(name, this);
                    session.addClient(client);

                    String sessionId;
                    do {
                        sessionId = Utils.generateLobbyId(LOBBY_ID_LENGTH);
                    } while (sessions.containsKey(sessionId));

                    sessions.put(sessionId, session);

                    LobbyCreateResponse response = new LobbyCreateResponse(name, sessionId);

                    byte[] bytes = SerializationUtils.INSTANCE.serialize(response);
                    send(client, bytes, bytes.length);
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
