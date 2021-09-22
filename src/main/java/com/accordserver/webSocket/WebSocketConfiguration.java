package com.accordserver.webSocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import static com.accordserver.util.Constants.PRIVATE_SERVER_CHAT_ENDPOINT;
import static com.accordserver.util.Constants.SYSTEM_ENDPOINT;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        System.out.println("A");
        webSocketHandlerRegistry.addHandler(newSystemWebSocketHandler(), SYSTEM_ENDPOINT).setAllowedOrigins("*");
        webSocketHandlerRegistry.addHandler(newPrivateServerChatWebSocketHandler(), PRIVATE_SERVER_CHAT_ENDPOINT).setAllowedOrigins("*");
    }

    @Bean
    public SystemWebSocketHandler newSystemWebSocketHandler() {
        System.out.println("B: newSystemWebSocketHandler");
        return new SystemWebSocketHandler();
    }

    @Bean
    public PrivateServerChatWebSocketHandler newPrivateServerChatWebSocketHandler() {
        System.out.println("B: newPrivateServerChatWebSocketHandler");
        return new PrivateServerChatWebSocketHandler();
    }
}