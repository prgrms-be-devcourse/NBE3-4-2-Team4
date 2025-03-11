package com.NBE3_4_2_Team4.domain.chat.chat.service

import com.NBE3_4_2_Team4.domain.chat.chat.dto.ChatDto
import com.NBE3_4_2_Team4.domain.chat.chat.entity.Chat
import com.NBE3_4_2_Team4.domain.chat.chat.repository.ChatRepository
import com.NBE3_4_2_Team4.domain.chat.chatRoom.entity.ChatRoom
import com.NBE3_4_2_Team4.domain.chat.chatRoom.service.ChatRoomService
import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.domain.member.member.service.MemberService
import com.NBE3_4_2_Team4.global.exceptions.ServiceException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChatService(
    private val chatRepository: ChatRepository,
    private val chatRoomService: ChatRoomService,
    private val memberService: MemberService
) {
    fun count(): Long {
        return chatRepository.count()
    }

    fun findById(id: Long): Chat {
        return chatRepository.findById(id).orElseThrow {
            ServiceException(
                "404-1",
                "해당 채팅이 존재하지 않습니다."
            )
        }
    }

    @Transactional
    fun write(
        chatRoomId: Long,
        senderUsername: String,
        content: String
    ): ChatDto {
        val chatRoom = chatRoomService.findById(chatRoomId)
        val sender = memberService.findByUsername(senderUsername) ?: throw ServiceException("404-1", "해당 유저를 찾을 수 없습니다.")

        return ChatDto(save(chatRoom, sender, content))
    }

    fun save(chatRoom: ChatRoom, sender: Member, content: String): Chat {
        val chat = Chat(
            chatRoom,
            sender,
            content
        )

        return chatRepository.save(chat)
    }

    @Transactional(readOnly = true)
    fun items(chatRoomId: Long): List<ChatDto> {
        val chatRoom = chatRoomService.findById(chatRoomId)

        return chatRepository.findByChatRoom(chatRoom)
            .map { ChatDto(it) }
    }
}