package com.NBE3_4_2_Team4.domain.member.member.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record NicknameUpdateRequestDto (
        @NotNull
        @NotEmpty
        @Length(min = 2)
        String newNickname
){
}
