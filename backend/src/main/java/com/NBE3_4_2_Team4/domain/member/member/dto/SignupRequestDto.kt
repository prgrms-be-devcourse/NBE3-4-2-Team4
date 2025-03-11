package com.NBE3_4_2_Team4.domain.member.member.dto

import com.fasterxml.jackson.annotation.JsonCreator
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.hibernate.validator.constraints.Length

data class SignupRequestDto @JsonCreator constructor(
    @NotNull
    @NotBlank
    @Length(min = 5)
    val email: String,

    @NotNull
    @NotBlank
    @Length(min = 2)
    val nickname: String
){
}