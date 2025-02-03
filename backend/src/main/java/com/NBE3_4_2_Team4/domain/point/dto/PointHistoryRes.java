package com.NBE3_4_2_Team4.domain.point.dto;

import com.NBE3_4_2_Team4.domain.point.entity.PointCategory;
import com.NBE3_4_2_Team4.domain.point.entity.PointHistory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PointHistoryRes {
    private final Long amount;
    private final LocalDateTime createdAt;
    private final String counterAccountUsername;
    private final PointCategory pointCategory;

    public static PointHistoryRes from(PointHistory pointHistory) {
        return PointHistoryRes.builder()
                .amount(pointHistory.getAmount())
                .createdAt(pointHistory.getCreatedAt())
                .counterAccountUsername(pointHistory.getCounterMember().getUsername())
                .pointCategory(pointHistory.getPointCategory())
                .build();
    }
}
