package com.NBE3_4_2_Team4.global.config

import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.messaging.simp.stomp.StompHeaders
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.socket.client.WebSocketClient
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class WebSocketTest {
    @Test
    fun `웹소켓 연결 테스트`() {
        val webSocketClient: WebSocketClient = StandardWebSocketClient()

        val stompClient = WebSocketStompClient(webSocketClient)

        val destination = "ws://localhost:8080/ws"

        val stompSessionHandler = object : StompSessionHandlerAdapter() {
            // 연결 후 실행할 코드
            override fun afterConnected(session: StompSession, connectedHeaders: StompHeaders) {
                println("WebSocket 연결 성공!")

                // 연결 후 메시지 전송
                val message = "테스트 메세지!"
                session.send("/chat/sendMessage", message)
            }

            // 서버로부터 수신한 메시지 처리
            override fun handleFrame(headers: StompHeaders, payload: Any?) {
                println("Received message: $payload")
            }
        }

        // 5. WebSocket 연결 (connect() 메서드를 사용)
        stompClient.connect(destination, stompSessionHandler)
    }
}