package com.NBE3_4_2_Team4.domain.asset.main.controller;

import com.NBE3_4_2_Team4.domain.asset.factory.AssetServiceFactory;
import com.NBE3_4_2_Team4.domain.asset.main.dto.AdminAssetTransferReq;
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetCategory;
import com.NBE3_4_2_Team4.domain.asset.main.service.AssetService;
import com.NBE3_4_2_Team4.global.rsData.RsData;
import com.NBE3_4_2_Team4.standard.base.Empty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/asset")
@Tag(name = "AssetAdminController", description = "API 어드민 전용 재화 컨트롤러")
public class AssetAdminController {
    private final AssetServiceFactory assetServiceFactory;

    @Operation(summary="유저에게 재화를 적립")
    @PutMapping("/accumulate")
    public RsData<Empty> accumulateForMember(@RequestBody @Validated AdminAssetTransferReq reqDto) {
        AssetService assetService = assetServiceFactory.getService(reqDto.getAssetType());
        assetService.adminAccumulate(reqDto.getUsername(), reqDto.getAmount(), reqDto.getAdminAssetCategoryId());

        return new RsData<>(
                "200-1",
                "적립 성공",
                new Empty()
        );
    }

    @Operation(summary="유저에게서 재화를 차감")
    @PutMapping("/deduct")
    public RsData<Empty> deductFromMember(@RequestBody @Validated AdminAssetTransferReq reqDto) {
        AssetService assetService = assetServiceFactory.getService(reqDto.getAssetType());
        assetService.adminDeduct(reqDto.getUsername(), reqDto.getAmount(), reqDto.getAdminAssetCategoryId());

        return new RsData<>(
                "200-1",
                "차감 성공",
                new Empty()
        );
    }
}
