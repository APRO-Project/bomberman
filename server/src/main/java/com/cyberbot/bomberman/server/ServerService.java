package com.cyberbot.bomberman.server;

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
        sessions.add(new Session());
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

                boolean inSession = false;
                for (Session session : sessions) {
                    if (session.handlePacket(client, packet.getData(), packet.getLength())) {
                        inSession = true;
                    }
                }

                if (!inSession) {
                    // TODO: Read packet and assign or create session
                    sessions.get(0).addClient(client);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void tick() {
        sessions.forEach(session -> {
            byte[] state = session.getState();
            session.getClients().forEach(client -> {
                try {
                    send(client, state, state.length);
                } catch (IOException e) {
                    session.removeClient(client);
                }
            });
        });
    }

    private void send(ClientConnection client, byte[] data, int length) throws IOException {
        DatagramPacket packet = new DatagramPacket(data, 0, length, client.getAddress(), client.getPort());
        socket.send(packet);
    }
}
