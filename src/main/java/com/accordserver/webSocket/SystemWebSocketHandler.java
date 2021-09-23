package com.accordserver.webSocket;

import com.accordserver.accessingdatamysql.user.User;
import com.github.cliftonlabs.json_simple.JsonObject;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SystemWebSocketHandler extends TextWebSocketHandler {

    private final List<WebSocketSession> webSocketSessions = new ArrayList<>();

    // add a new connection / session when a client starts one
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        webSocketSessions.add(session);
    }

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
        System.out.println("SystemWebSocketHandler webSocket-error-status: " + status.getReason() + " : " + status.getCode());
        webSocketSessions.remove(session);
    }

    @Bean
    public void sendUserJoined(User user) {
        System.out.println("user joined: " + user.getName());
        // broadcast userJoined message to all connections / clients

        JsonObject jsonObject = new JsonObject();
        jsonObject.put("action", "userJoined");
        JsonObject userData = new JsonObject();
        userData.put("name", user.getName());
        userData.put("id", String.valueOf(user.getId()));
        jsonObject.put("data", userData);

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
        System.out.println("user left: " + user.getName());
        // broadcast userLeft message to all connections / clients

        JsonObject jsonObject = new JsonObject();
        jsonObject.put("action", "userLeft");
        JsonObject userData = new JsonObject();
        userData.put("name", user.getName());
        userData.put("id", String.valueOf(user.getId()));
        jsonObject.put("data", userData);

        for (WebSocketSession webSocketSession : webSocketSessions) {
            try {
                webSocketSession.sendMessage(new TextMessage(jsonObject.toJson()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}