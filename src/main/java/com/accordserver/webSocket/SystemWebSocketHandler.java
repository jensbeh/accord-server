package com.accordserver.webSocket;

import com.accordserver.accessingdatamysql.categories.Categories;
import com.accordserver.accessingdatamysql.server.Server;
import com.accordserver.accessingdatamysql.user.User;
import com.github.cliftonlabs.json_simple.JsonObject;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SystemWebSocketHandler extends TextWebSocketHandler {

    private final List<WebSocketSession> systemWebSocketSessions = new ArrayList<>();
    private final Map<String, WebSocketSession> serverSystemWebSocketSessions = new HashMap<>();

    // add a new connection / session when a client starts one
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        if (session.getUri().getQuery() != null) {
            // System server session
            String serverId = session.getUri().getQuery().substring(session.getUri().getQuery().indexOf("serverId=") + 9);
            serverSystemWebSocketSessions.put(serverId, session);
        } else {
            // System session
            systemWebSocketSessions.add(session);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//        System.out.println("E: SystemWebSocketHandler: " + message + " --- " + session.getUri().getQuery());
        if (!message.getPayload().equals("noop")) {

        }
        // broadcast all messages to all connections / clients
        for (WebSocketSession webSocketSession : systemWebSocketSessions) {
            webSocketSession.sendMessage(message);
        }
    }

    // removes the connection when a client closes it.
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        System.out.println("SystemWebSocketHandler webSocket-error-status: " + status.getReason() + " : " + status.getCode());
        systemWebSocketSessions.remove(session);
    }

    @Bean
    public void sendUserJoined(User user) {
        System.out.println("user joined: " + user.getName());
        // broadcast userJoined message to all connections / clients

        JsonObject jsonObject = new JsonObject();
        jsonObject.put("action", "userJoined");
        JsonObject userData = new JsonObject();
        userData.put("name", user.getName());
        userData.put("id", user.getId());
        jsonObject.put("data", userData);

        for (WebSocketSession webSocketSession : systemWebSocketSessions) {
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
        userData.put("id", user.getId());
        jsonObject.put("data", userData);

        for (WebSocketSession webSocketSession : systemWebSocketSessions) {
            try {
                webSocketSession.sendMessage(new TextMessage(jsonObject.toJson()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Bean
    public void sendServerUpdated(Server updatedServer) {
        // broadcast serverUpdate message to all connections / clients

        JsonObject jsonObject = new JsonObject();
        jsonObject.put("action", "serverUpdated");
        JsonObject serverData = new JsonObject();
        serverData.put("name", updatedServer.getName());
        serverData.put("id", updatedServer.getId());
        jsonObject.put("data", serverData);

        // send updatedName to all members with jsonObject
        for (Map.Entry<String, WebSocketSession> entry : serverSystemWebSocketSessions.entrySet()) {
            if (entry.getKey().equals(updatedServer.getId())) {
                try {
                    entry.getValue().sendMessage(new TextMessage(jsonObject.toJson()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("server updated: " + updatedServer.getName() + " " + updatedServer.getId());
    }

    @Bean
    public void sendCategoryCreated(Server currentServer, Categories createdCategory) {
        // broadcast categoryCreated message to all connections / clients

        JsonObject jsonObject = new JsonObject();
        jsonObject.put("action", "categoryCreated");
        JsonObject categoryData = new JsonObject();
        categoryData.put("server", currentServer.getId());
        categoryData.put("id", createdCategory.getId());
        categoryData.put("name", createdCategory.getName());
        jsonObject.put("data", categoryData);

        // send updatedName to all members with jsonObject
        for (Map.Entry<String, WebSocketSession> entry : serverSystemWebSocketSessions.entrySet()) {
            if (entry.getKey().equals(currentServer.getId())) {
                try {
                    entry.getValue().sendMessage(new TextMessage(jsonObject.toJson()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("category created: " + createdCategory.getName() + " " + createdCategory.getId());
    }

    @Bean
    public void sendCategoryUpdated(Categories updatedCategory) {
        // broadcast categoryUpdated message to all connections / clients

//        JsonObject jsonObject = new JsonObject();
//        jsonObject.put("action", "serverUpdated");
//        JsonObject serverData = new JsonObject();
//        serverData.put("name", updatedServer.getName());
//        serverData.put("id", updatedServer.getId());
//        jsonObject.put("data", serverData);
//
//        // send updatedName to all members with jsonObject
//        for (Map.Entry<String, WebSocketSession> entry : serverSystemWebSocketSessions.entrySet()) {
//            if (entry.getKey().equals(updatedServer.getId())) {
//                try {
//                    entry.getValue().sendMessage(new TextMessage(jsonObject.toJson()));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        System.out.println("server updated: " + updatedServer.getName() + " " + updatedServer.getId());
    }
}