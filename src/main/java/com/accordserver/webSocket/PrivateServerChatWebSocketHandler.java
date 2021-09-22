package com.accordserver.webSocket;

import com.accordserver.accessingdatamysql.channels.Channels;
import com.accordserver.accessingdatamysql.channels.ChannelsRepository;
import com.accordserver.accessingdatamysql.messages.Messages;
import com.accordserver.accessingdatamysql.messages.MessagesRepository;
import com.accordserver.accessingdatamysql.server.Server;
import com.accordserver.accessingdatamysql.server.ServerRepository;
import com.accordserver.accessingdatamysql.user.User;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import org.hibernate.Hibernate;
import org.hibernate.type.BigIntegerType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class PrivateServerChatWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private ServerRepository serverRepository;

    @Autowired
    private ChannelsRepository channelsRepository;

    @Autowired
    private MessagesRepository messagesRepository;

    private final Map<String, WebSocketSession> userWebSocketSessionsMap = new HashMap<>();

    // add a new connection / session when a client starts one
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        if (session.getUri().getQuery().contains("&")) {
            // server ws
            String username = session.getUri().getQuery().substring(session.getUri().getQuery().indexOf("user=") + 5, session.getUri().getQuery().indexOf("&"));
            String serverId = session.getUri().getQuery().substring(session.getUri().getQuery().indexOf("serverId=") + 9);
            String key = username + "&" + serverId;
            userWebSocketSessionsMap.put(key, session);
        } else {
            // private chat ws
            String username = session.getUri().getQuery().substring(session.getUri().getQuery().indexOf("=") + 1);
            userWebSocketSessionsMap.put(username, session);
        }
    }

    @Override
    @Transactional
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("E: ChatWebSocketHandler: " + message.getPayload() + " --- " + session.getUri().getQuery() + " : " + session.getUri().getPath());
        // broadcast all messages to all connections / clients
        if (!message.getPayload().equals("noop")) {

            if (session.getUri().getQuery().contains("&")) {
                // server Message
                System.out.println("server Message");

                String senderName = session.getUri().getQuery().substring(session.getUri().getQuery().indexOf("user=") + 5, session.getUri().getQuery().indexOf("&"));
                String serverId = session.getUri().getQuery().substring(session.getUri().getQuery().indexOf("serverId=") + 9);

                JsonObject messageJson = Jsoner.deserialize(message.getPayload(), new JsonObject());

                String channelId = (String) messageJson.get("channel");
                String messageText = (String) messageJson.get("message");

                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                long currentTime = timestamp.getTime();

                Channels currentChannel = channelsRepository.findById(Integer.parseInt(channelId));
                Messages newMessage = new Messages(messageText, senderName, currentTime, currentChannel);

                currentChannel.setMessages(newMessage); // XXX
                channelsRepository.save(currentChannel);

                Messages reloadMessage = messagesRepository.findBytimestampMessage(currentTime);

                JsonObject readyJsonObject = new JsonObject();
                readyJsonObject.put("id", String.valueOf(reloadMessage.getId())); // messageId
                readyJsonObject.put("channel", channelId);
                readyJsonObject.put("timestamp", currentTime);
                readyJsonObject.put("from", senderName);
                readyJsonObject.put("text", messageText);

                // send message to all members with readyJsonObject
                Server currentServer = serverRepository.findById(Integer.parseInt(serverId));
                for (User member : currentServer.getMembers()) {
                    String key = member.getName() + "&" + serverId;
                    if (userWebSocketSessionsMap.containsKey(key)) { // send to ALL users in the channel
                        userWebSocketSessionsMap.get(key).sendMessage(new TextMessage(readyJsonObject.toJson()));
                    }
                }

            } else {
                // private Message
                System.out.println("private Message");

                String senderName = session.getUri().getQuery().substring(session.getUri().getQuery().indexOf("=") + 1);

                JsonObject messageJson = Jsoner.deserialize(message.getPayload(), new JsonObject());

                String channel = (String) messageJson.get("channel");
                String receiverName = (String) messageJson.get("to");
                String messageText = (String) messageJson.get("message");

                JsonObject readyJsonObject = new JsonObject();
                readyJsonObject.put("channel", channel);
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                readyJsonObject.put("timestamp", timestamp.getTime());
                readyJsonObject.put("message", messageText);
                readyJsonObject.put("from", senderName);
                readyJsonObject.put("to", receiverName);

                userWebSocketSessionsMap.get(senderName).sendMessage(new TextMessage(readyJsonObject.toJson()));
                if (userWebSocketSessionsMap.containsKey(receiverName)) {
                    userWebSocketSessionsMap.get(receiverName).sendMessage(new TextMessage(readyJsonObject.toJson()));
                } else {
                    System.out.println("User is not online!");
                }
            }
        }
    }

    // removes the connection when a client closes it.
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("F: status: " + status.getReason() + " : " + status.getCode());
        userWebSocketSessionsMap.remove(session);
    }


}