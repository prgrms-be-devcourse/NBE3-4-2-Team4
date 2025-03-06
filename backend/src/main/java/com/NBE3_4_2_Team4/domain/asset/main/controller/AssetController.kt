package com.NBE3_4_2_Team4.domain.asset.main.controller

import com.NBE3_4_2_Team4.domain.asset.factory.AssetServiceFactory
import com.NBE3_4_2_Team4.domain.asset.main.dto.AssetHistoryReq
import com.NBE3_4_2_Team4.domain.asset.main.dto.AssetHistoryRes
import com.NBE3_4_2_Team4.domain.asset.main.dto.AssetTransferReq
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetCategory
import com.NBE3_4_2_Team4.domain.asset.main.service.AssetHistoryService
import com.NBE3_4_2_Team4.global.rsData.RsData
import com.NBE3_4_2_Team4.global.security.AuthManager
import com.NBE3_4_2_Team4.standard.base.Empty
import com.NBE3_4_2_Team4.standard.dto.PageDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import lombok.RequiredArgsConstructor
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/asset")
@Tag(name = "AssetController", description = "API 재화 컨트롤러")
class AssetController (
    private val assetServiceFactory: AssetServiceFactory,
    private val assetHistoryService: AssetHistoryService
) {

    companion object {
        private const val ASSET_HISTORY_SIZE = 10
    }

    @PutMapping("/transfer")
    @Operation(summary = "재화 송금 기능")
    fun transfer(@Valid @RequestBody reqDto: AssetTransferReq): RsData<Empty> {
        val member = AuthManager.getNonNullMember()
        val assetService = assetServiceFactory.getService(reqDto.assetType)
        assetService.transfer(member.username, reqDto.username, reqDto.amount, AssetCategory.TRANSFER)

        return RsData(
            "200-1",
            "송금 성공",
            Empty()
        )
    }

    @GetMapping
    @Operation(summary = "재화 기록 조회(날짜 & 카테고리 필터포함)")
    fun getAssetHistories(@ModelAttribute @Valid assetHistoryReq: AssetHistoryReq): RsData<PageDto<AssetHistoryRes>> {
        val member = AuthManager.getNonNullMember()
        val assetHistories =
            assetHistoryService.getHistoryPageWithFilter(member, ASSET_HISTORY_SIZE, assetHistoryReq)

        return RsData(
            "200-1",
            "OK",
            assetHistories
        )
    }
}
