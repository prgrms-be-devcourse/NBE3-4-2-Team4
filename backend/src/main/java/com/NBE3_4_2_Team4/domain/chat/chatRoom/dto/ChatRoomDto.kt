package com.NBE3_4_2_Team4.domain.chat.chat.dto

import com.NBE3_4_2_Team4.domain.chat.chat.entity.Chat
import com.NBE3_4_2_Team4.domain.chat.chatRoom.entity.ChatRoom
import java.time.LocalDateTime

class ChatRoomDto(
    val id: Long,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
    val name: String,
    val chat: MutableList<Chat>,
    val memberList: MutableList<String>

) {
    constructor(chatRoom: ChatRoom) : this(
        id = chatRoom.id,
        createdAt = chatRoom.createdAt,
        modifiedAt = chatRoom.modifiedAt,
        name = chatRoom.name,
        chat = chatRoom.chat,
        memberList = chatRoom.member
            .map { it.nickname }
            .toMutableList()
    )
}