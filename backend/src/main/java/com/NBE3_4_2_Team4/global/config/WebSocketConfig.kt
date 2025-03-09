package com.NBE3_4_2_Team4.global.config

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig: WebSocketMessageBrokerConfigurer {
    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        // 메시지를 구독할 경로
        config.enableSimpleBroker("/topic")

        // 클라이언트가 메시지를 보낼 때 붙이는 prefix
        config.setApplicationDestinationPrefixes("/chat")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        // WebSocket 연결 엔드포인트 설정
        registry.addEndpoint("/ws")
            .setAllowedOrigins("*")
            .withSockJS()
    }
}