package com.NBE3_4_2_Team4.domain.member.member.dto

import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length
import org.jetbrains.annotations.NotNull

data class MemberThumbnailInfoResponseDto @JsonCreator constructor(
    @JsonProperty("id") @NotNull
    val id:Long,

    @NotNull
    val role: Member.Role,

    @JsonProperty("nickname")
    @NotNull
    @NotBlank
    @Length(min = 2)
    val nickname: String
) {
}