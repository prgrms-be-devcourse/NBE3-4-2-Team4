package com.NBE3_4_2_Team4.domain.asset.point.dto;

import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetCategory;
import com.NBE3_4_2_Team4.domain.asset.point.validation.annotation.ValidPointHistoryReq;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@ValidPointHistoryReq
@Getter
@Setter
public class AssetHistoryReq {
    @NotNull
    @Min(1)
    private Integer page=1;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    private AssetCategory assetCategory;

    public LocalDateTime getStartDateTime() {
        return startDate != null ? startDate.atStartOfDay() : null;
    }

    public LocalDateTime getEndDateTime() {
        return endDate != null ? endDate.atTime(LocalTime.MAX) : null;
    }

}
