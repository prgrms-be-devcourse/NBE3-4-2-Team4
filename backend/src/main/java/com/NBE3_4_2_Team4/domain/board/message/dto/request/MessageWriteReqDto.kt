package com.NBE3_4_2_Team4.domain.board.message.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

data class MessageWriteReqDto(
        @field:NotNull
        @field:Length(min = 2)
        val title: String,

        @field:NotNull
        @field:Length(min = 2)
        val content: String,

        @field:NotNull
        @field:JsonProperty("receiverName")
        val receiverName: String
)
