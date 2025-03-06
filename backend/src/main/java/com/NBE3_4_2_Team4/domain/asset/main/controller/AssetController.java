package com.NBE3_4_2_Team4.domain.asset.main.controller;

import com.NBE3_4_2_Team4.domain.asset.factory.AssetServiceFactory;
import com.NBE3_4_2_Team4.domain.asset.main.dto.*;
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetCategory;
import com.NBE3_4_2_Team4.domain.asset.main.service.AssetHistoryService;
import com.NBE3_4_2_Team4.domain.asset.main.service.AssetService;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.global.rsData.RsData;
import com.NBE3_4_2_Team4.standard.base.Empty;
import com.NBE3_4_2_Team4.standard.dto.PageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.NBE3_4_2_Team4.global.security.AuthManager.getNonNullMember;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/asset")
@Tag(name = "AssetController", description = "API 재화 컨트롤러")
public class AssetController {
    private final AssetServiceFactory assetServiceFactory;
    private final AssetHistoryService assetHistoryService;
    private final static int ASSET_HISTORY_SIZE = 10;

    @PutMapping("/transfer")
    @Operation(summary="재화 송금 기능")
    public RsData<Empty> transfer(@Valid @RequestBody AssetTransferReq reqDto) {
        Member member = getNonNullMember();
        AssetService assetService = assetServiceFactory.getService(reqDto.getAssetType());
        assetService.transfer(member.getUsername(), reqDto.getUsername(), reqDto.getAmount(), AssetCategory.TRANSFER);

        return new RsData<>(
                "200-1",
                "송금 성공",
                new Empty()
        );
    }

    @GetMapping()
    @Operation(summary="재화 기록 조회(날짜 & 카테고리 필터포함)")
    public RsData<PageDto<AssetHistoryRes>> getAssetHistories(@Valid @ModelAttribute AssetHistoryReq assetHistoryReq) {
        Member member = getNonNullMember();
        PageDto<AssetHistoryRes> assetHistories =
                assetHistoryService.getHistoryPageWithFilter(member, ASSET_HISTORY_SIZE, assetHistoryReq);

        return new RsData<>(
                "200-1",
                "OK",
                assetHistories
        );
    }

    @PatchMapping("/deposit")
    @Operation(summary = "재화 적립 신청")
    public RsData<Long> deposit(@RequestBody AssetDepositReq reqDto) {

        Member member = getNonNullMember();

        AssetService service = assetServiceFactory.getService(reqDto.getAssetType());
        Long assetHistoryId = service.accumulate(member.getUsername(), reqDto.getAmount(), reqDto.getAssetCategory());

        return new RsData<>(
                "200-1",
                "OK",
                assetHistoryId
        );
    }
    
    @PatchMapping("/refund")
    @Operation(summary = "재화 반환 신청")
    public RsData<Long> refund(@RequestBody AssetRefundReq reqDto) {

        Member member = getNonNullMember();

        AssetService service = assetServiceFactory.getService(reqDto.getAssetType());
        Long assetHistoryId = service.deduct(member.getUsername(), reqDto.getAmount(), reqDto.getAssetCategory());

        return new RsData<>(
                "200-1",
                "OK",
                assetHistoryId
        );
    }
}
