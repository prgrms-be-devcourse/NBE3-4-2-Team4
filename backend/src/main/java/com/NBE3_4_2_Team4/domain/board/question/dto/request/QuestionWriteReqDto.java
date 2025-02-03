package com.NBE3_4_2_Team4.domain.board.question.dto.request;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record QuestionWriteReqDto(
        @NotNull @Length(min = 2)
        String title,
        @NotNull @Length(min = 2)
        String content,
        @NotNull
        Long categoryId
) {}
