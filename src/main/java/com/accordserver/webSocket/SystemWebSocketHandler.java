package com.accordserver.webSocket;

import com.accordserver.accessingdatamysql.categories.Categories;
import com.accordserver.accessingdatamysql.channels.Channels;
import com.accordserver.accessingdatamysql.messages.Messages;
import com.accordserver.accessingdatamysql.server.Server;
import com.accordserver.accessingdatamysql.user.User;
import com.github.cliftonlabs.json_simple.JsonArray;
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
    //    private final Map<String, WebSocketSession> serverSystemWebSocketSessions = new HashMap<>();
    // serverId
    private final Map<String, Map<String, WebSocketSession>> serverIdUserKeysWebSocketSessions = new HashMap<>();

    // add a new connection / session when a client starts one
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        if (session.getUri().getQuery() != null) {
            // System server session
            String serverId = session.getUri().getQuery().substring(session.getUri().getQuery().indexOf("serverId=") + 9);
            String userKey = session.getHandshakeHeaders().get("userKey").toString();

            if (serverIdUserKeysWebSocketSessions.containsKey(serverId)) {
                serverIdUserKeysWebSocketSessions.get(serverId).put(userKey, session);
            } else {
                Map<String, WebSocketSession> userKeySession = new HashMap<>();
                userKeySession.put(userKey, session);
                serverIdUserKeysWebSocketSessions.put(serverId, userKeySession);
            }
            System.out.println("SystemWebSocket server created: " + userKey + " " + serverId);

        } else {
            // System session
            systemWebSocketSessions.add(session);

            String userKey = session.getHandshakeHeaders().get("userKey").toString();
            System.out.println("SystemWebSocket private created: " + userKey);
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

        if (session.getUri().getQuery() != null) {
            // System server session
            String serverId = session.getUri().getQuery().substring(session.getUri().getQuery().indexOf("serverId=") + 9);
            String userKey = session.getHandshakeHeaders().get("userKey").toString();
            if (serverIdUserKeysWebSocketSessions.containsKey(serverId)) {
                // remove user session
                serverIdUserKeysWebSocketSessions.get(serverId).remove(userKey);
                // remove server map if empty
                if (serverIdUserKeysWebSocketSessions.get(serverId).size() == 0) {
                    serverIdUserKeysWebSocketSessions.remove(serverId);
                }
            }
            System.out.println("ServerSystemWebSocket server removed: " + userKey + " " + serverId + " : " + status.getReason() + " : " + status.getCode());
        } else {
            // System session
            systemWebSocketSessions.remove(session);

            String userKey = session.getHandshakeHeaders().get("userKey").toString();
            System.out.println("SystemWebSocket server removed: " + userKey + " : " + status.getReason() + " : " + status.getCode());
        }
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

        // iterate over all server of the user and check if other user are also there and send there the message to all
        for (Server server : user.getServers()) {
            if (serverIdUserKeysWebSocketSessions.containsKey(server.getId())) {
                // send userJoined to all members in this server with jsonObject
                for (Map.Entry<String, WebSocketSession> userKeySessionEntry : serverIdUserKeysWebSocketSessions.get(server.getId()).entrySet()) {
                    try {
                        userKeySessionEntry.getValue().sendMessage(new TextMessage(jsonObject.toJson()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
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

        // iterate over all server of the user and check if other user are also there and send there the message to all
        for (Server server : user.getServers()) {
            if (serverIdUserKeysWebSocketSessions.containsKey(server.getId())) {
                // send userLeft to all members in this server with jsonObject
                for (Map.Entry<String, WebSocketSession> userKeySessionEntry : serverIdUserKeysWebSocketSessions.get(server.getId()).entrySet()) {
                    try {
                        userKeySessionEntry.getValue().sendMessage(new TextMessage(jsonObject.toJson()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
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

        // send updatedName to all members in this server with jsonObject
        for (Map.Entry<String, WebSocketSession> userKeySessionEntry : serverIdUserKeysWebSocketSessions.get(updatedServer.getId()).entrySet()) {
            try {
                userKeySessionEntry.getValue().sendMessage(new TextMessage(jsonObject.toJson()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("server updated: " + updatedServer.getName() + " " + updatedServer.getId());
    }

    @Bean
    public void sendCategoryCreated(Server currentServer, Categories newCategory) {
        // broadcast categoryCreated message to all connections / clients

        JsonObject jsonObject = new JsonObject();
        jsonObject.put("action", "categoryCreated");
        JsonObject categoryData = new JsonObject();
        categoryData.put("server", currentServer.getId());
        categoryData.put("id", newCategory.getId());
        categoryData.put("name", newCategory.getName());
        jsonObject.put("data", categoryData);

        // send newCategory to all members in this server with jsonObject
        for (Map.Entry<String, WebSocketSession> userKeySessionEntry : serverIdUserKeysWebSocketSessions.get(currentServer.getId()).entrySet()) {
            try {
                userKeySessionEntry.getValue().sendMessage(new TextMessage(jsonObject.toJson()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("category created: " + newCategory.getName() + " " + newCategory.getId());
    }

    @Bean
    public void sendCategoryUpdated(Server currentServer, Categories updatedCategory) {
        // broadcast categoryUpdated message to all connections / clients

        JsonObject jsonObject = new JsonObject();
        jsonObject.put("action", "categoryUpdated");
        JsonObject categoryData = new JsonObject();
        categoryData.put("server", currentServer.getId());
        categoryData.put("id", updatedCategory.getId());
        categoryData.put("name", updatedCategory.getName());
        jsonObject.put("data", categoryData);

        // send updatedName to all members in this server with jsonObject
        for (Map.Entry<String, WebSocketSession> userKeySessionEntry : serverIdUserKeysWebSocketSessions.get(currentServer.getId()).entrySet()) {
            try {
                userKeySessionEntry.getValue().sendMessage(new TextMessage(jsonObject.toJson()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("category updated: " + updatedCategory.getName() + " " + updatedCategory.getId());
    }

    @Bean
    public void sendChannelCreated(Server currentServer, Categories currentCategory, Channels newChannel) {
        // broadcast channelCreated message to all connections / clients

        JsonObject jsonObject = new JsonObject();
        jsonObject.put("action", "channelCreated");

        JsonObject channelData = new JsonObject();
        channelData.put("id", newChannel.getId());
        channelData.put("name", newChannel.getName());
        channelData.put("type", newChannel.getType());
        channelData.put("privileged", newChannel.isPrivileged());
        channelData.put("category", currentCategory.getId());

        jsonObject.put("data", channelData);

        // send newChannel to all members in this server with jsonObject
        for (Map.Entry<String, WebSocketSession> userKeySessionEntry : serverIdUserKeysWebSocketSessions.get(currentServer.getId()).entrySet()) {
            try {
                userKeySessionEntry.getValue().sendMessage(new TextMessage(jsonObject.toJson()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("channel created: " + newChannel.getName() + " " + newChannel.getId());
    }

    @Bean
    public void sendChannelUpdated(Server currentServer, Categories currentCategory, Channels updatedChannel) {
        // broadcast channelUpdated message to all connections / clients

        JsonObject jsonObject = new JsonObject();
        jsonObject.put("action", "channelUpdated");

        JsonObject channelData = new JsonObject();
        channelData.put("category", currentCategory.getId());
        channelData.put("id", updatedChannel.getId());
        channelData.put("name", updatedChannel.getName());
        channelData.put("type", updatedChannel.getType());
        channelData.put("privileged", updatedChannel.isPrivileged());

        // add privileged member
        JsonArray jsonArrayPrivilegedMember = new JsonArray();
        for (User user : updatedChannel.getPrivilegedMember()) {
            jsonArrayPrivilegedMember.add(user.getId());
        }
        channelData.put("members", jsonArrayPrivilegedMember);

        jsonObject.put("data", channelData);

        // send updatedChannel to all members in this server with jsonObject
        for (Map.Entry<String, WebSocketSession> userKeySessionEntry : serverIdUserKeysWebSocketSessions.get(currentServer.getId()).entrySet()) {
            try {
                userKeySessionEntry.getValue().sendMessage(new TextMessage(jsonObject.toJson()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("channel updated: " + updatedChannel.getName() + " " + updatedChannel.getId());
    }

    @Bean
    public void sendMessageUpdated(Server currentServer, Categories currentCategory, Channels currentChannel, Messages updatedMessage) {
        // broadcast messageUpdated message to all connections / clients

        JsonObject jsonObject = new JsonObject();
        jsonObject.put("action", "messageUpdated");

        JsonObject messageData = new JsonObject();
        messageData.put("id", updatedMessage.getId());
        messageData.put("text", updatedMessage.getContent());

        jsonObject.put("data", messageData);

        // send updatedMessage to all members in this server with jsonObject
        for (Map.Entry<String, WebSocketSession> userKeySessionEntry : serverIdUserKeysWebSocketSessions.get(currentServer.getId()).entrySet()) {
            try {
                userKeySessionEntry.getValue().sendMessage(new TextMessage(jsonObject.toJson()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("message updated: " + updatedMessage.getContent() + " " + updatedMessage.getId());
    }

    @Bean
    public void sendUserArrived(Server currentServer, User arrivedUser) {
        // broadcast messageUpdated message to all connections / clients

        JsonObject jsonObject = new JsonObject();
        jsonObject.put("action", "userArrived");

        JsonObject arrivedUserData = new JsonObject();
        arrivedUserData.put("id", arrivedUser.getId());
        arrivedUserData.put("name", arrivedUser.getName());
        arrivedUserData.put("online", arrivedUser.isOnline());

        jsonObject.put("data", arrivedUserData);

        // send arrivedUserData to all members in this server with jsonObject
        for (Map.Entry<String, WebSocketSession> userKeySessionEntry : serverIdUserKeysWebSocketSessions.get(currentServer.getId()).entrySet()) {
            try {
                userKeySessionEntry.getValue().sendMessage(new TextMessage(jsonObject.toJson()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("user arrived: " + arrivedUser.getName() + " " + arrivedUser.getId() + " at server: " + currentServer.getName() + " " + currentServer.getId());
    }

    @Bean
    public void sendServerDeleted(Server deletedServer, User currentUser) {
        // broadcast serverDeleted message to all connections / clients

        JsonObject jsonObject = new JsonObject();
        jsonObject.put("action", "serverDeleted");

        JsonObject deletedServerData = new JsonObject();
        deletedServerData.put("id", deletedServer.getId());
        deletedServerData.put("name", deletedServer.getName());

        jsonObject.put("data", deletedServerData);

        // send serverDeletedData to all members in this server with jsonObject
        for (Map.Entry<String, WebSocketSession> userKeySessionEntry : serverIdUserKeysWebSocketSessions.get(deletedServer.getId()).entrySet()) {
            try {
                userKeySessionEntry.getValue().sendMessage(new TextMessage(jsonObject.toJson()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("deleted Server: " + deletedServer.getName() + " " + deletedServer.getId());
    }

    @Bean
    public void sendCategoryDeleted(Server currentServer, Categories deletedCategory, User currentUser) {
        // broadcast categoryDeleted message to all connections / clients

        JsonObject jsonObject = new JsonObject();
        jsonObject.put("action", "categoryDeleted");

        JsonObject deletedCategoryData = new JsonObject();
        deletedCategoryData.put("id", deletedCategory.getId());
        deletedCategoryData.put("name", deletedCategory.getName());
        deletedCategoryData.put("server", currentServer.getId());

        jsonObject.put("data", deletedCategoryData);

        // send deletedCategoryData to all members in this server with jsonObject
        for (Map.Entry<String, WebSocketSession> userKeySessionEntry : serverIdUserKeysWebSocketSessions.get(currentServer.getId()).entrySet()) {
            try {
                userKeySessionEntry.getValue().sendMessage(new TextMessage(jsonObject.toJson()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("deleted Category: " + deletedCategory.getName() + " " + deletedCategory.getId());
    }

    @Bean
    public void sendChannelDeleted(Server currentServer, Categories currentCategory, Channels deletedChannel, User currentUser) {
        // broadcast channelDeleted message to all connections / clients

        JsonObject jsonObject = new JsonObject();
        jsonObject.put("action", "channelDeleted");

        JsonObject deletedChannelData = new JsonObject();
        deletedChannelData.put("id", deletedChannel.getId());
        deletedChannelData.put("name", deletedChannel.getName());
        deletedChannelData.put("category", currentCategory.getId());
        deletedChannelData.put("server", currentServer.getId());

        jsonObject.put("data", deletedChannelData);

        // send deletedChannelData to all members in this server with jsonObject
        for (Map.Entry<String, WebSocketSession> userKeySessionEntry : serverIdUserKeysWebSocketSessions.get(currentServer.getId()).entrySet()) {
            try {
                userKeySessionEntry.getValue().sendMessage(new TextMessage(jsonObject.toJson()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("deleted Channel: " + deletedChannel.getName() + " " + deletedChannel.getId());
    }

    @Bean
    public void sendMessageDeleted(Server currentServer, Categories currentCategory, Channels currentChannel, Messages deletedMessage, User currentUser) {
        // broadcast messageDeleted message to all connections / clients

        JsonObject jsonObject = new JsonObject();
        jsonObject.put("action", "messageDeleted");

        JsonObject deletedMessageData = new JsonObject();
        deletedMessageData.put("id", deletedMessage.getId());
        deletedMessageData.put("channel", currentChannel.getId());
        deletedMessageData.put("category", currentCategory.getId());
        deletedMessageData.put("server", currentServer.getId());

        jsonObject.put("data", deletedMessageData);

        // send deletedMessageData to all members in this server with jsonObject
        for (Map.Entry<String, WebSocketSession> userKeySessionEntry : serverIdUserKeysWebSocketSessions.get(currentServer.getId()).entrySet()) {
            try {
                userKeySessionEntry.getValue().sendMessage(new TextMessage(jsonObject.toJson()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("deleted Message: " + deletedMessage.getContent() + " " + deletedMessage.getFromUser() + " " + deletedMessage.getId());
    }
}