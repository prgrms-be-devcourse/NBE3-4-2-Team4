package com.NBE3_4_2_Team4.domain.member.member.dto;

import com.NBE3_4_2_Team4.global.mail.state.MailState;
import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record SignupResponseDto(
        @NotNull
        MailState mailState
) {
    @JsonCreator
    public SignupResponseDto{}
}
