package com.NBE3_4_2_Team4.domain.board.question.dto;

import com.NBE3_4_2_Team4.domain.board.question.entity.QuestionCategory;
import lombok.Getter;

@Getter
public class QuestionCategoryDto {
    private final Long id;
    private final String name;

    public QuestionCategoryDto(QuestionCategory category) {
        this.id = category.getId();
        this.name = category.getName();
    }
}
