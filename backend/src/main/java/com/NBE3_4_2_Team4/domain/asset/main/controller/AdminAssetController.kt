package com.NBE3_4_2_Team4.domain.asset.main.controller

import com.NBE3_4_2_Team4.domain.asset.factory.AssetServiceFactory
import com.NBE3_4_2_Team4.domain.asset.main.dto.AdminAssetTransferReq
import com.NBE3_4_2_Team4.domain.asset.main.service.AssetHistoryService
import com.NBE3_4_2_Team4.global.rsData.RsData
import com.NBE3_4_2_Team4.standard.base.Empty
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import lombok.RequiredArgsConstructor
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/asset")
@Tag(name = "AdminAssetController", description = "API 어드민 전용 재화 컨트롤러")
class AdminAssetController (
    private val assetServiceFactory: AssetServiceFactory
){


    @Operation(summary = "유저에게 재화를 적립")
    @PutMapping("/accumulate")
    fun accumulateForMember(@RequestBody @Validated reqDto: AdminAssetTransferReq): RsData<Empty> {
        val assetService = assetServiceFactory.getService(reqDto.assetType)
        assetService.adminAccumulate(reqDto.username, reqDto.amount, reqDto.adminAssetCategoryId)

        return RsData(
            "200-1",
            "적립 성공",
            Empty()
        )
    }

    @Operation(summary = "유저에게서 재화를 차감")
    @PutMapping("/deduct")
    fun deductFromMember(@RequestBody @Validated reqDto: AdminAssetTransferReq): RsData<Empty> {
        val assetService = assetServiceFactory.getService(reqDto.assetType)
        assetService.adminDeduct(reqDto.username, reqDto.amount, reqDto.adminAssetCategoryId)

        return RsData(
            "200-1",
            "차감 성공",
            Empty()
        )
    }
}
