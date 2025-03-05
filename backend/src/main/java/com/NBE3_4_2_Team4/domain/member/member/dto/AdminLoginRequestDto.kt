package com.NBE3_4_2_Team4.domain.member.member.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.hibernate.validator.constraints.Length


data class AdminLoginRequestDto @JsonCreator constructor(
    @field:JsonProperty("adminUsername")
    @param:JsonProperty("adminUsername")
    @field:NotNull
    @field:NotBlank
    @field:Length(min = 5)
    val adminUsername: String,

    @field:JsonProperty("password")
    @param:JsonProperty("password")
    @field:NotNull
    @field:NotBlank
    @field:Length(min = 4)
    val password: String
)