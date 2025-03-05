package com.NBE3_4_2_Team4.domain.board.answer.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class AnswerRequestDto(
        @field:NotBlank
        @field:Size(min = 2)
        @JvmField //Todo
        val content: String
)
