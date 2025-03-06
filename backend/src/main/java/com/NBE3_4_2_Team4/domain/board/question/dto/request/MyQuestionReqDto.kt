package com.NBE3_4_2_Team4.domain.board.question.dto.request;

import jakarta.validation.constraints.NotNull;

data class MyQuestionReqDto(
        @field:NotNull
        val username: String
) {}
