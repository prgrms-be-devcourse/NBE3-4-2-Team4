package com.NBE3_4_2_Team4.domain.chat.chatRoom.controller

import com.NBE3_4_2_Team4.domain.chat.chat.dto.ChatRoomDto
import com.NBE3_4_2_Team4.domain.chat.chatRoom.dto.ChatRoomRequstDto
import com.NBE3_4_2_Team4.domain.chat.chatRoom.service.ChatRoomService
import com.NBE3_4_2_Team4.global.rsData.RsData
import com.NBE3_4_2_Team4.standard.base.Empty
import com.NBE3_4_2_Team4.standard.dto.PageDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@Tag(name = "채팅방 관리", description = "채팅방 관련 API")
@RequestMapping("/api/chatRooms")
class ChatRoomController(
    private val chatRoomService: ChatRoomService
) {
    @PostMapping
    @Operation(summary = "채팅방 생성", description = "새로운 채팅방을 생성합니다.")
    fun create(
        @RequestBody @Valid reqBody: ChatRoomRequstDto,
    ): RsData<ChatRoomDto> {
        val chatRoom = chatRoomService.create(reqBody.recipientUsername, reqBody.name)

        return RsData(
            "201-1",
            "${chatRoom.id}번 채팅방이 생성되었습니다.",
            chatRoom
        )
    }

    @GetMapping
    @Operation(summary = "자신의 채팅방 다건 조회", description = "로그인 한 회원의 채팅방 전체 조회")
    fun myChatRooms(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") pageSize: Int
    ): PageDto<ChatRoomDto> {
        return PageDto(
            chatRoomService.myChatRooms(page, pageSize)
        )
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "채팅방 나가기", description = "채팅방 id를 기준으로 해당 채팅방의 member에서 삭제됩니다.")
    fun exitChatRoom(
        @PathVariable id: Long
    ): RsData<Empty> {
        chatRoomService.exitChatRoom(id)

        return RsData(
            "200-2",
            "${id}번 채팅방에서 나갔습니다."
        )
    }

    @GetMapping("/{id}")
    @Operation(summary = "채팅방 단건 조회", description = "채팅방 Id를 기준으로 채팅방을 찾습니다.")
    fun getChatRoom(
        @PathVariable id: Long
    ): ChatRoomDto {
        return chatRoomService.getChatRoom(id)
    }
}