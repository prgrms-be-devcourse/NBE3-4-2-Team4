package com.NBE3_4_2_Team4.domain.chat.chat.controller

import com.NBE3_4_2_Team4.domain.chat.chat.dto.ChatDto
import com.NBE3_4_2_Team4.domain.chat.chat.dto.ChatRequestDto
import com.NBE3_4_2_Team4.domain.chat.chat.service.ChatService
import jakarta.validation.Valid
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody

@Controller
class ChatWebSocketController(
    private val chatService: ChatService,
    private val messagingTemplate: SimpMessagingTemplate
) {
    @MessageMapping("/sendMessage")
    fun sendMessage(
        @RequestBody @Valid reqBody: ChatRequestDto
    ): ChatDto {
        // 채팅을 서비스에서 처리하고 메시지를 저장
        val chatDto = chatService.write(reqBody.chatRoomId, reqBody.senderUsername, reqBody.content)

        // 메시지 전송: 클라이언트에게 메시지 발송
        messagingTemplate.convertAndSend("/topic/chatRooms/${reqBody.chatRoomId}", chatDto)

        return chatDto
    }
}