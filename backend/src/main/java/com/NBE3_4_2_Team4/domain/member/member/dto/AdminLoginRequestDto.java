package com.NBE3_4_2_Team4.domain.member.member.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
public record AdminLoginRequestDto(
        @JsonProperty("adminUsername")
        @NotNull
        @NotBlank
        @Length(min = 5)
        String adminUsername,

        @JsonProperty("password")
        @NotNull
        @NotBlank
        @Length(min = 2)
        String password
) {
        @JsonCreator
        public AdminLoginRequestDto {}
}
