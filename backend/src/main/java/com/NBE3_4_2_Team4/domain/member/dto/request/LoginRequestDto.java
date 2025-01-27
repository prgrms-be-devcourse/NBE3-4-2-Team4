package com.NBE3_4_2_Team4.domain.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record LoginRequestDto(
        @NotBlank
        @Pattern(regexp = "^[a-zA-Z0-9+-_.]+@[a-zA-Z0-9-]+|.[a-zA-Z0-9-.]+$")
        String email,

        @NotBlank
        String password
) {
}
