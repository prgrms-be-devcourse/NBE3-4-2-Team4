package com.NBE3_4_2_Team4.domain.member.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
public record AdminLoginRequestDto(
        @NotNull
        @NotBlank
        @Length(min = 5)
        String adminUsername,

        @NotNull
        @NotBlank
        @Length(min = 5)
        String password
) {
}
