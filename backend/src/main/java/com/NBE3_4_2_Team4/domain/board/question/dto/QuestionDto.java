package com.NBE3_4_2_Team4.domain.board.question.dto;

import com.NBE3_4_2_Team4.domain.board.question.entity.Question;
import com.NBE3_4_2_Team4.domain.board.question.entity.QuestionCategory;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class QuestionDto {
    private final Long id;
    private final String title;
    private final String content;
    private final String name;
    private final String categoryName;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public QuestionDto(Question question) {
        this.id = question.getId();
        this.title = question.getTitle();
        this.content = question.getContent();
        this.name = question.getAuthor().getNickname();
        this.categoryName = question.getCategory().getName();
        this.createdAt = question.getCreatedAt();
        this.modifiedAt = question.getModifiedAt();
    }
}
