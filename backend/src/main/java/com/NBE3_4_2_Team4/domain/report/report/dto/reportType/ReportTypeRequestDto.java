package com.NBE3_4_2_Team4.domain.report.report.dto.reportType;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ReportTypeRequestDto(
        @NotBlank
        String name
) {
    @JsonCreator
    public ReportTypeRequestDto{}
}
