package com.NBE3_4_2_Team4.domain.chat.chat.entity

import com.NBE3_4_2_Team4.domain.chat.chatRoom.entity.ChatRoom
import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.global.jpa.entity.BaseTime
import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne
import java.time.LocalDateTime

@Entity
class Chat : BaseTime {
    @ManyToOne //Todo : (fetch = FetchType.LAZY)
    lateinit var chatRoom: ChatRoom

    @ManyToOne //Todo : (fetch = FetchType.LAZY)
    lateinit var sender: Member

    lateinit var content: String
    var isRead: Boolean = false
    var readAt: LocalDateTime? = null

    constructor(
        chatRoom: ChatRoom,
        sender: Member,
        content: String
    ) {
        this.chatRoom = chatRoom
        this.sender = sender
        this.content = content
    }
}