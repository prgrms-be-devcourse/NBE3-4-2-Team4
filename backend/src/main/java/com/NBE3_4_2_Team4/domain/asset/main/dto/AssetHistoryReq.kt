package com.NBE3_4_2_Team4.domain.asset.main.dto

import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetCategory
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetType
import com.NBE3_4_2_Team4.domain.asset.main.validation.annotation.ValidAssetHistoryReq
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@ValidAssetHistoryReq
data class AssetHistoryReq(
    @field:NotNull
    @field:Min(1)
    val page: Int = 1,

    val assetType: AssetType? = null,

    val assetCategory: AssetCategory? = null,

    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val startDate: LocalDate? = null,

    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val endDate: LocalDate? = null
) {
    @get:JsonIgnore
    val startDateTime: LocalDateTime?
        get() = startDate?.atStartOfDay()

    @get:JsonIgnore
    val endDateTime: LocalDateTime?
        get() = endDate?.atTime(LocalTime.MAX)
}
