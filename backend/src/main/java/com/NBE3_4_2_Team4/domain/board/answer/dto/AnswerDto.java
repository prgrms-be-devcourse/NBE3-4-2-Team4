package com.NBE3_4_2_Team4.domain.board.answer.dto;

import com.NBE3_4_2_Team4.domain.board.answer.entity.Answer;
import lombok.Getter;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

@Getter
public class AnswerDto {
    @NonNull
    private long id;

    @NonNull
    private LocalDateTime createdAt;

    @NonNull
    private LocalDateTime modifiedAt;

    @NonNull
    private final long questionId;

    @NonNull
    private String content;

    public AnswerDto(Answer answer) {
        this.id = answer.getId();
        this.createdAt = answer.getCreatedAt();
        this.modifiedAt = answer.getModifiedAt();
        this.questionId = answer.getQuestion().getId();
        this.content = answer.getContent();
    }
}
