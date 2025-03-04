package com.NBE3_4_2_Team4.domain.report.report.dto.report;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReportResponseDto (
        String reporterNickname,
        String reportedNickname,
        String reportTypeName,
        String title,
        String content,
        LocalDateTime reportTime
) {
    @JsonCreator
    public ReportResponseDto{}
}
