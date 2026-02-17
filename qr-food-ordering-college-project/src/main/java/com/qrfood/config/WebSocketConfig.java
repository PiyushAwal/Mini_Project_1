package com.qrfood.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocketConfig
 *
 * Configures WebSocket + STOMP for real-time communication.
 *
 * Flow:
 *   1. Customer places order  -> Server receives via REST
 *   2. Server pushes order    -> /topic/kitchen   (kitchen screen listens)
 *   3. Server pushes order    -> /topic/manager   (manager screen listens)
 *   4. Kitchen updates status -> Server pushes to /topic/table/{n} (customer sees update)
 *
 * WebSocket Endpoint : ws://localhost:8080/ws
 * SockJS Fallback    : http://localhost:8080/ws  (for browsers that block WS)
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Register the WebSocket endpoint that clients connect to.
     * SockJS provides fallback for older browsers.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    /**
     * Configure message routing:
     *
     * /app/...        -> Messages FROM client TO server (via @MessageMapping)
     * /topic/...      -> Messages FROM server TO client (subscriptions)
     *
     * Subscription topics:
     *   /topic/kitchen      -> Kitchen screen subscribes here
     *   /topic/manager      -> Manager screen subscribes here
     *   /topic/table/{n}    -> Customer at table N subscribes here
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic");
    }
}
