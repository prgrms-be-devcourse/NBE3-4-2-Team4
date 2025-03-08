package com.NBE3_4_2_Team4.domain.chat.chat.dto

import com.NBE3_4_2_Team4.domain.chat.chat.entity.Chat
import java.time.LocalDateTime

class ChatDto(
    val id: Long,
    val createdAt: LocalDateTime,
    val chatRoomId: Long,
    val senderId: Long,
    val senderName: String,
    val content: String,
    val isRead: Boolean,
    val readAt: LocalDateTime?

) {
    constructor(chat: Chat) : this(
        id = chat.id,
        createdAt = chat.createdAt,
        chatRoomId = chat.chatRoom.id,
        senderId = chat.sender.id,
        senderName = chat.sender.nickname,
        content = chat.content,
        isRead = chat.isRead,
        readAt = chat.readAt
    )
}