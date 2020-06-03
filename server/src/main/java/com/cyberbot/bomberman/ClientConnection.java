package com.cyberbot.bomberman;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Objects;

public class ClientConnection {
    private final int port;
    private final InetAddress address;

    public ClientConnection(int port, InetAddress address) {
        this.port = port;
        this.address = address;
    }

    public static ClientConnection fromDatagramPacket(DatagramPacket packet) {
        return new ClientConnection(packet.getPort(), packet.getAddress());
    }

    public int getPort() {
        return port;
    }

    public InetAddress getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientConnection that = (ClientConnection) o;

        if (port != that.port) return false;
        return Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        int result = port;
        result = 31 * result + (address != null ? address.hashCode() : 0);
        return result;
    }
}
