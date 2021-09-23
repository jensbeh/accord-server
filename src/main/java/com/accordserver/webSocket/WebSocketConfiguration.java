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

    /**
     * Add Handlers to the different Endpoints. Handler will handle the incoming messages and also can send messages.
     * .setAllowedOrigins("*") can limit the origin link from where the client is coming.
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(newSystemWebSocketHandler(), SYSTEM_ENDPOINT).setAllowedOrigins("*");
        webSocketHandlerRegistry.addHandler(newPrivateServerChatWebSocketHandler(), PRIVATE_SERVER_CHAT_ENDPOINT).setAllowedOrigins("*");
    }

    /**
     * Creates a new Handler which can handle all system messages.
     */
    @Bean
    public SystemWebSocketHandler newSystemWebSocketHandler() {
        return new SystemWebSocketHandler();
    }

    /**
     * Creates a new Handler which can handle all chat messages.
     */
    @Bean
    public PrivateServerChatWebSocketHandler newPrivateServerChatWebSocketHandler() {
        return new PrivateServerChatWebSocketHandler();
    }
}