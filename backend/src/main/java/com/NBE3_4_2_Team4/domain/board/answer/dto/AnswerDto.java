package com.NBE3_4_2_Team4.domain.board.answer.dto;

import com.NBE3_4_2_Team4.domain.board.answer.entity.Answer;
import lombok.Getter;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

@Getter
public class AnswerDto {
    @NonNull
    private final long id;

    @NonNull
    private final LocalDateTime createdAt;

    @NonNull
    private final LocalDateTime modifiedAt;

    @NonNull
    private final long questionId;

    @NonNull
    private final long authorId;

    @NonNull
    private final String authorName;

    @NonNull
    private final String content;

    public AnswerDto(Answer answer) {
        this.id = answer.getId();
        this.createdAt = answer.getCreatedAt();
        this.modifiedAt = answer.getModifiedAt();
        this.questionId = answer.getQuestion().getId();
        this.authorId = answer.getAuthor().getId();
        this.authorName = answer.getAuthor().getNickname();
        this.content = answer.getContent();
    }
}
