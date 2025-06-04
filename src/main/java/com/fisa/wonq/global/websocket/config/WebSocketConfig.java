package com.fisa.wonq.global.websocket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 웹소켓 엔드포인트 설정
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")  // CORS 설정, 필요에 따라 제한
                .withSockJS();  // SockJS 지원 추가
                
        // Native WebSocket 엔드포인트도 추가 (SockJS를 사용하지 않는 클라이언트를 위함)
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 클라이언트로 메시지를 다시 전송하는 목적지 prefix
        registry.enableSimpleBroker("/topic");
        
        // 클라이언트에서 서버로 메시지를 전송하는 목적지 prefix
        registry.setApplicationDestinationPrefixes("/app");
    }
}
