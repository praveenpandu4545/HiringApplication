package com.praveen.socket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.*;

@Component
@Slf4j
public class WebSocketEventListener {

    @EventListener
    public void handleConnect(SessionConnectEvent event) {

        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(event.getMessage());

        log.info("WebSocket connected: {}", accessor.getSessionId());
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {

        log.info("WebSocket disconnected: {}", event.getSessionId());
    }
}