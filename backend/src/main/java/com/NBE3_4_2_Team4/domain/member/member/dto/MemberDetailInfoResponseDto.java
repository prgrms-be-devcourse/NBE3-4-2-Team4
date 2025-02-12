package com.NBE3_4_2_Team4.domain.member.member.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Builder;

@Builder
public record MemberDetailInfoResponseDto(
        String username,
        String nickname,
        long point,
        long questionSize,
        long answerSize
) {
    @JsonCreator
    public MemberDetailInfoResponseDto{}
}
