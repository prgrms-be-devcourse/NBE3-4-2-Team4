package com.NBE3_4_2_Team4.domain.chat.chat.repository

import com.NBE3_4_2_Team4.domain.chat.chat.entity.Chat
import com.NBE3_4_2_Team4.domain.chat.chatRoom.entity.ChatRoom
import org.springframework.data.jpa.repository.JpaRepository

interface ChatRepository : JpaRepository<Chat, Long> {
    fun findByChatRoom(chatRoom: ChatRoom): List<Chat>
}