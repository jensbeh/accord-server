package com.accordserver.udpserver;

import java.net.InetAddress;

public class ServerClient {
    public String name;
    public InetAddress address;
    public int port;

    public ServerClient(String name, InetAddress address, int port) {
        this.name = name;
        this.address = address;
        this.port = port;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }
}
