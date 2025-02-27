package com.NBE3_4_2_Team4.domain.board.message.dto;

import com.NBE3_4_2_Team4.domain.board.message.entity.Message;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MessageDto {
    private final String senderName;
    private final String receiverName;
    private final boolean isChecked;
    private final LocalDateTime createdAt;
    private final String title;
    private final String content;

    public MessageDto(Message message) {
        this.senderName = message.getSender().getUsername();
        this.receiverName = message.getReceiver().getUsername();
        this.isChecked = message.isChecked();
        this.createdAt = message.getCreatedAt();
        this.title = message.getTitle();
        this.content = message.getContent();
    }
}
