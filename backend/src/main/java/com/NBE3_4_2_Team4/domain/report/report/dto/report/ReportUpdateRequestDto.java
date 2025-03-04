package com.NBE3_4_2_Team4.domain.report.report.dto.report;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Builder;

@Builder
public record ReportUpdateRequestDto(
        Long reportId,
        Long reportTypeId,
        String title,
        String content
) {
    @JsonCreator
    public ReportUpdateRequestDto{}
}
