package com.NBE3_4_2_Team4.domain.chat.chatRoom.entity

import com.NBE3_4_2_Team4.domain.chat.chat.entity.Chat
import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.global.exceptions.ServiceException
import com.NBE3_4_2_Team4.global.jpa.entity.BaseTime
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToMany

@Entity
class ChatRoom : BaseTime {
    lateinit var name: String

    @OneToMany(mappedBy = "chatRoom", cascade = [CascadeType.PERSIST, CascadeType.REMOVE], orphanRemoval = true)
    val chat: MutableList<Chat> = mutableListOf()

    @ManyToMany
    val member: MutableList<Member> = mutableListOf()

    constructor(
        name: String
    ) {
        this.name = name
    }
}