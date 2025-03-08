package com.NBE3_4_2_Team4.domain.chat.chatRoom.repository

import com.NBE3_4_2_Team4.domain.chat.chatRoom.entity.ChatRoom
import org.springframework.data.jpa.repository.JpaRepository

interface ChatRoomRepsitory : JpaRepository<ChatRoom, Long>