package com.NBE3_4_2_Team4.domain.board.answer.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record AnswerRequestDto(
        @NotBlank
        @Length(min = 2)
        String content
) {
}
