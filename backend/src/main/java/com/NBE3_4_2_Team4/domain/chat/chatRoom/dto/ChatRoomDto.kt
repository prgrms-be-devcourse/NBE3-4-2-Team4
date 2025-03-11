package com.NBE3_4_2_Team4.domain.chat.chat.dto

import com.NBE3_4_2_Team4.domain.chat.chatRoom.entity.ChatRoom
import java.time.LocalDateTime

class ChatRoomDto(
    val id: Long,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
    val name: String,
    val chatCount: Int,
    val newChatCount: Int,
    val isClosed: Boolean,
    val memberList: List<String>

) {
    constructor(chatRoom: ChatRoom, memberList: List<String>) : this(
        id = chatRoom.id,
        createdAt = chatRoom.createdAt,
        modifiedAt = chatRoom.modifiedAt,
        name = chatRoom.name,
        chatCount = chatRoom.chat.size,
        newChatCount = chatRoom.chat
            .stream()
            .filter{!it.isRead}
            .count()
            .toInt(),
        isClosed = chatRoom.isClosed,
        memberList = memberList
    )
}