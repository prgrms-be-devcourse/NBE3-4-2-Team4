package com.NBE3_4_2_Team4.domain.board.answer.dto;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record AnswerCreateReqBody(
        @NotNull
        @Length(min = 2)
        String content
) {
}
