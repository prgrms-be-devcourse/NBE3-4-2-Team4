package com.NBE3_4_2_Team4.domain.point.controller;

import com.NBE3_4_2_Team4.domain.point.dto.PointTransferReq;
import com.NBE3_4_2_Team4.domain.point.entity.PointCategory;
import com.NBE3_4_2_Team4.domain.point.service.PointService;
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

//관리자 권한으로 고객의 포인트를 조작하는 컨트롤러
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/points")
@Tag(name = "PointAdminController", description = "API 어드민 전용 포인트 컨트롤러")
public class PointAdminController {
    private final PointService pointService;

    @Operation(summary="유저에게 포인트를 적립")
    @PutMapping("/accumulate")
    public RsData<Empty> accumulateForMember(@RequestBody @Validated PointTransferReq reqDto) {

        pointService.accumulate(reqDto.getUsername(), reqDto.getAmount(), PointCategory.ADMIN);

        return new RsData<>(
                "200-1",
                "적립 성공",
                new Empty()
        );
    }

    @Operation(summary="유저에게서 포인트를 차감")
    @PutMapping("/deduct")
    public RsData<Empty> deductFromMember(@RequestBody @Validated PointTransferReq reqDto) {

        pointService.deduct(reqDto.getUsername(), reqDto.getAmount(), PointCategory.ADMIN);
        return new RsData<>(
                "200-1",
                "차감 성공",
                new Empty()
        );
    }
}
