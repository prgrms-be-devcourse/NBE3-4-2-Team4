package com.NBE3_4_2_Team4.domain.chat.chatRoom.entity

import com.NBE3_4_2_Team4.domain.chat.chat.entity.Chat
import com.NBE3_4_2_Team4.global.jpa.entity.BaseTime
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.OneToMany

@Entity
class ChatRoom : BaseTime {
    lateinit var name: String

    @OneToMany(mappedBy = "chatRoom", cascade = [CascadeType.PERSIST, CascadeType.REMOVE], orphanRemoval = true)
    val chat: MutableList<Chat> = mutableListOf()

    var isClosed: Boolean = false

    constructor(
        name: String
    ) {
        this.name = name
    }
}