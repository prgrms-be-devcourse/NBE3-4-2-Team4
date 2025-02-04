package com.NBE3_4_2_Team4.domain.board.question.dto;

import com.NBE3_4_2_Team4.domain.board.answer.dto.AnswerDto;
import com.NBE3_4_2_Team4.domain.board.question.entity.Question;
import lombok.Getter;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

@Getter
public class QuestionDto {
    @NonNull
    private final Long id;
    @NonNull
    private final String title;
    @NonNull
    private final String content;
    @NonNull
    private final String name;
    @NonNull
    private final String categoryName;
    @NonNull
    private final LocalDateTime createdAt;
    @NonNull
    private final LocalDateTime modifiedAt;
    private final AnswerDto selectedAnswer;
    @NonNull
    private final boolean closed;
    @NonNull
    private final long point;

    public QuestionDto(Question question) {
        this.id = question.getId();
        this.title = question.getTitle();
        this.content = question.getContent();
        this.name = question.getAuthor().getNickname();
        this.categoryName = question.getCategory().getName();
        this.createdAt = question.getCreatedAt();
        this.modifiedAt = question.getModifiedAt();
        this.selectedAnswer = new AnswerDto(question.getSelectedAnswer());
        this.closed = question.isClosed();
        this.point = question.getPoint();
    }
}
