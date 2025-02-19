package com.NBE3_4_2_Team4.domain.member.member.dto;

import com.NBE3_4_2_Team4.domain.member.member.entity.asset.Point;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Builder;

@Builder
public record MemberDetailInfoResponseDto(
        String username,
        String nickname,
        Point point,
        long questionSize,
        long answerSize
) {
    @JsonCreator
    public MemberDetailInfoResponseDto{}
}
