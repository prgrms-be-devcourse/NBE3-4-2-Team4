package com.NBE3_4_2_Team4.domain.member.member.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
public record NicknameUpdateRequestDto (
        @JsonProperty("newNickname")
        @NotNull
        @NotEmpty
        @Length(min = 2)
        String newNickname
){
        @JsonCreator
        public NicknameUpdateRequestDto{}
}
