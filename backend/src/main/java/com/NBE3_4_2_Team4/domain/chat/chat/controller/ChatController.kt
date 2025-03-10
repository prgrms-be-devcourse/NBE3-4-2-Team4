package com.NBE3_4_2_Team4.domain.chat.chatRoom.controller

import com.NBE3_4_2_Team4.domain.chat.chat.dto.ChatDto
import com.NBE3_4_2_Team4.domain.chat.chat.dto.ChatRequestDto
import com.NBE3_4_2_Team4.domain.chat.chat.service.ChatService
import com.NBE3_4_2_Team4.global.rsData.RsData
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@Tag(name = "채팅 메세지 관리", description = "채팅 메세지 관련 API")
@RequestMapping("/api/chatRooms/{chatRoomId}/chats")
class ChatController(
    private val chatService: ChatService
) {
    @PostMapping
    @Operation(summary = "채팅 쓰기", description = "새로운 채팅 메세지를 작성합니다")
    fun write(
        @PathVariable chatRoomId: Long,
        @RequestBody @Valid reqBody: ChatRequestDto,
    ): RsData<ChatDto> {
        val chat = chatService.write(chatRoomId, reqBody.senderUsername, reqBody.content)

        return RsData(
            "201-1",
            "${chat.id}번 메세지가 전송되었습니다.",
            chat
        )
    }

    @GetMapping
    @Operation(summary = "채팅방 기준 채팅 다건 조회", description = "해당 채팅방 내 채팅들 전체 조회")
    fun items(
        @PathVariable chatRoomId: Long
    ): List<ChatDto> {
        return chatService.items(chatRoomId)
    }
}