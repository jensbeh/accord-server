package com.accordserver.udpserver;

import org.json.JSONObject;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.accordserver.util.Constants.UDP_PORT;

public class UdpServer implements Runnable {
    private boolean running = false;

    private Thread run, send, receive;

    private DatagramSocket socket;
    private Map<String, Map<String, ServerClient>> channelClientsMap = new HashMap<>();

    public UdpServer() {
        try {
            socket = new DatagramSocket(UDP_PORT);
        } catch (SocketException e) {
            e.printStackTrace();
            return;
        }

        // start server thread
        run = new Thread(this, "Server");
        run.start();
    }

    public void run() {
        running = true;
        System.out.println("UdpServer started on port " + UDP_PORT);
        receive();
    }

    /**
     * Method to receive the udp packet with json object and audio data
     */
    private void receive() {
        receive = new Thread("Receive") {
            public void run() {
                while (running) {
                    byte[] data = new byte[1279];
                    DatagramPacket packet = new DatagramPacket(data, data.length);
                    try {
                        socket.receive(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    byte[] informationDataBytes = packet.getData();
                    byte[] jsonDataBytes = Arrays.copyOfRange(informationDataBytes, 0, 255);

                    JSONObject jsonData = new JSONObject(new String(jsonDataBytes));

                    String channelId = jsonData.getString("channel");
                    String userName = jsonData.getString("name");

                    checkForNewClientAndChannel(channelId, userName, packet);

                    sendToChannel(channelId, informationDataBytes);
                }
            }
        };
        receive.start();
    }

    /**
     * Method to route the udp packet to all channel members
     *
     * @param channelId id of the channel where the audio packet should be route to
     * @param informationDataBytes udp packet which should be route
     */
    private void sendToChannel(String channelId, byte[] informationDataBytes) {
        for (ServerClient serverClient : channelClientsMap.get(channelId).values()) {
            send(informationDataBytes, serverClient.getAddress(), serverClient.getPort());
        }
    }

    /**
     * Method to start a new Thread where the udp data is sent to client
     *
     * @param data udp packet data to sent
     * @param address address of the client where to send
     * @param port port of the client where to send
     */
    private void send(byte[] data, InetAddress address, int port) {
        send = new Thread("Send") {
            public void run() {
                DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
                try {
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        send.start();
    }

    /**
     * Method to check if the client / channel is already logged.
     * If not, there will be created one with all information.
     *
     * @param channelId id of the channel where the audio packet should be route to
     * @param userName username of the user who sends the packet
     * @param packet packet which the user sends to the server / channel
     */
    private void checkForNewClientAndChannel(String channelId, String userName, DatagramPacket packet) {
        if (channelClientsMap.containsKey(channelId)) {
            if (!channelClientsMap.get(channelId).containsKey(userName)) {
                channelClientsMap.get(channelId).put(userName, new ServerClient(userName, packet.getAddress(), packet.getPort()));
            }
        } else {
            Map<String, ServerClient> client = new HashMap<>();
            client.put(userName, new ServerClient(userName, packet.getAddress(), packet.getPort()));
            channelClientsMap.put(channelId, client);
        }
    }

    /**
     * Removes the client from the list (no more data is sent to him) and delete the channel if he is the last user in this channel.
     */
    @Bean
    public void removeUdpClient() {

    }
}
