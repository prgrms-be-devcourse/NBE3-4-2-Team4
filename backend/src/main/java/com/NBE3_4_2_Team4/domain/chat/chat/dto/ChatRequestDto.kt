package com.NBE3_4_2_Team4.domain.chat.chat.dto;


import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ChatRequestDto(
    @field:NotBlank
    @field:Size(min = 1)
    val content: String,

    val chatRoomId: Long
)
