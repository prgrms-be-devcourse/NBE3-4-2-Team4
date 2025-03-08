package com.NBE3_4_2_Team4.domain.chat.chatRoom.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ChatRoomRequstDto (
    @field:NotBlank
    @field:Size(min = 1)
    val name: String,

    @field:NotBlank
    val recipientUsername: String
)