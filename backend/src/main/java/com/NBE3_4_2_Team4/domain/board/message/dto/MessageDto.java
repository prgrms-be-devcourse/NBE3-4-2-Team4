package com.NBE3_4_2_Team4.domain.board.message.dto;

import com.NBE3_4_2_Team4.domain.board.message.entity.Message;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MessageDto {
    private final Member sender;
    private final Member receiver;
    private final boolean isChecked;
    private final LocalDateTime createdAt;
    private final String title;
    private final String content;

    public MessageDto(Message message) {
        this.sender = message.getSender();
        this.receiver = message.getReceiver();
        this.isChecked = message.isChecked();
        this.createdAt = message.getCreatedAt();
        this.title = message.getTitle();
        this.content = message.getContent();
    }
}
