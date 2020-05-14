package com.cyberbot.bomberman;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Session {
    private final List<ClientConnection> clients = new ArrayList<>();

    private String state = "";

    public boolean handlePacket(ClientConnection connection, byte[] data, int length) {
        if(!hasClient(connection)) {
            return false;
        }

        state += new String(data, 0, length, StandardCharsets.UTF_8);

        return true;
    }

    public byte[] getState() {
        return state.getBytes();
    }

    public Iterable<ClientConnection> getClients() {
        return clients;
    }

    public void addClient(ClientConnection connection) {
        clients.add(connection);
    }

    public boolean removeClient(ClientConnection connection) {
        return clients.remove(connection);
    }

    public boolean hasClient(ClientConnection connection) {
        return clients.contains(connection);
    }
}
