package com.NBE3_4_2_Team4.domain.member.member.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
public record SignupRequestDto(
        @NotNull
        @NotBlank
        @Length(min = 5)
        String email,

        @NotNull
        @NotBlank
        @Length(min = 2)
        String nickname
) {
    @JsonCreator
    public SignupRequestDto{}
}
