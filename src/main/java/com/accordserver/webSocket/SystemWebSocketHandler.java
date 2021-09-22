package com.accordserver.webSocket;

import com.accordserver.accessingdatamysql.user.User;
import com.github.cliftonlabs.json_simple.JsonObject;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SystemWebSocketHandler extends TextWebSocketHandler {

    private final List<WebSocketSession> webSocketSessions = new ArrayList<>();

    // add a new connection / session when a client starts one
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//        System.out.println("D: userKey: " + session.getHandshakeHeaders().get("userKey"));
        // send user is online!
        webSocketSessions.add(session);
    }


    // unterschiedliche packages mit kopien für alle ws
    // private system keine handleTextMessage?
    // eigene klasse für verschiedene sockethandler? (.addHandler(new SocketTextHandler)) (https://www.javainuse.com/spring/boot-websocket (min 6:33))
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//        System.out.println("E: SystemWebSocketHandler: " + message + " --- " + session.getUri().getQuery());

        // broadcast all messages to all connections / clients
        for (WebSocketSession webSocketSession : webSocketSessions) {
            webSocketSession.sendMessage(message);
        }
    }

    // removes the connection when a client closes it.
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("F: status: " + status.getReason() + " : " + status.getCode());
        // send user is offline!
        webSocketSessions.remove(session);
    }

    @Bean
    public void sendUserJoined(User user) {
        System.out.println("User is online! " + user.getName());
        // broadcast all messages to all connections / clients

        JsonObject jsonObject = new JsonObject();
        jsonObject.put("action", "userJoined"); // userLeft
        JsonObject userData = new JsonObject();
        userData.put("name", user.getName());
        userData.put("id", String.valueOf(user.getId()));
        jsonObject.put("data", userData); // userLeft

        for (WebSocketSession webSocketSession : webSocketSessions) {
            try {
                webSocketSession.sendMessage(new TextMessage(jsonObject.toJson()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Bean
    public void sendUserLeft(User user) {
        System.out.println("User left! " + user.getName());
        // broadcast all messages to all connections / clients

        JsonObject jsonObject = new JsonObject();
        jsonObject.put("action", "userLeft"); // userLeft
        JsonObject userData = new JsonObject();
        userData.put("name", user.getName());
        userData.put("id", String.valueOf(user.getId()));
        jsonObject.put("data", userData); // userLeft

        for (WebSocketSession webSocketSession : webSocketSessions) {
            try {
                webSocketSession.sendMessage(new TextMessage(jsonObject.toJson()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}