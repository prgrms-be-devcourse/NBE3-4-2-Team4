package com.NBE3_4_2_Team4.domain.report.report.dto.reportType;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ReportTypeUpdateRequestDto(
        @NotNull
        Long reportTypeId,

        @NotBlank
        String newName
) {
    @JsonCreator
    public ReportTypeUpdateRequestDto{}
}
