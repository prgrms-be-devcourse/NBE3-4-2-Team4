package com.NBE3_4_2_Team4.domain.member.member.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import org.hibernate.validator.constraints.Length

data class NicknameUpdateRequestDto @JsonCreator constructor(
    @JsonProperty("newNickname")
    @NotNull
    @NotEmpty
    @Length(min = 2)
    val newNickname: String
) {
}