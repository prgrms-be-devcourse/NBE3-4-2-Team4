package com.NBE3_4_2_Team4.domain.member.dto.request;

import lombok.Builder;

@Builder
public record SignUpRequestDto (
        String username,
        String password,
        String nickname
){
}
