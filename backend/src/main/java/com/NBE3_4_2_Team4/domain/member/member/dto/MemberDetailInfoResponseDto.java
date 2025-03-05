package com.NBE3_4_2_Team4.domain.member.member.dto;

import com.NBE3_4_2_Team4.domain.member.member.entity.asset.Cash;
import com.NBE3_4_2_Team4.domain.member.member.entity.asset.Point;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Builder;

@Builder
public record MemberDetailInfoResponseDto(
        String username,
        String nickname,
        Point point,
        Cash cash,
        long questionSize,
        long answerSize,
        String emailAddress,
        boolean isEmailVerified
) {
    @JsonCreator
    public MemberDetailInfoResponseDto{}

    public String emailAddress() {
        return maskEmail(emailAddress);
    }

    private static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email; // 예외적인 경우 그냥 반환
        }
        int atIndex = email.indexOf("@");
        if (atIndex <= 1) {
            return "*".repeat(atIndex) + email.substring(atIndex);
        }
        return email.substring(0, 2) + "*".repeat(atIndex - 2) + email.substring(atIndex);
    }
}
