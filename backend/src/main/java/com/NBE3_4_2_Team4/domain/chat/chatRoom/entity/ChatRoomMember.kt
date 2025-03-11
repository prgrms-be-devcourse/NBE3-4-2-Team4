package com.NBE3_4_2_Team4.domain.chat.chatRoom.entity

import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.global.jpa.entity.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne

@Entity
class ChatRoomMember : BaseEntity {
    @ManyToOne //Todo : (fetch = FetchType.LAZY)
    lateinit var chatRoom: ChatRoom

    @ManyToOne //Todo : (fetch = FetchType.LAZY)
    lateinit var member: Member

    constructor(
        chatRoom: ChatRoom,
        member: Member
    ) {
        this.chatRoom = chatRoom
        this.member = member
    }
}