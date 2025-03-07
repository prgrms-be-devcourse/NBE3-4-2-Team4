package com.NBE3_4_2_Team4.domain.board.message.dto;

import com.NBE3_4_2_Team4.domain.board.message.entity.Message;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
data class MessageDto(
    val id: Long,
    val senderName: String,
    val receiverName: String,
    val checked: Boolean,
    val createdAt: LocalDateTime,
    val title: String,
    val content: String
) {
    constructor(message: Message) : this(
        id = message.id,
        senderName = message.sender.nickname,
        receiverName = message.receiver.nickname,
        checked = message.checked,
        createdAt = message.createdAt,
        title = message.title,
        content = message.content
    )
}
