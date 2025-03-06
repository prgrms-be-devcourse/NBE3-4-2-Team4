package com.NBE3_4_2_Team4.domain.board.question.dto.request;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

data class QuestionCategoryReqDto(
        @field:NotNull
        @field:Length(min = 2)
        val name: String
)
