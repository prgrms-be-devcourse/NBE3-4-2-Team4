package com.NBE3_4_2_Team4.domain.point.controller;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.point.dto.PointTransferReq;
import com.NBE3_4_2_Team4.domain.point.entity.PointCategory;
import com.NBE3_4_2_Team4.domain.point.service.PointService;
import com.NBE3_4_2_Team4.global.rsData.RsData;
import com.NBE3_4_2_Team4.standard.base.Empty;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//관리자 권한으로 고객의 포인트를 조작하는 컨트롤러
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/products")
public class AdminPointController {
    private final PointService pointService;

    @PutMapping("/accumulate")
    public RsData<Empty> accumulateForMember(@RequestBody @Validated PointTransferReq reqDto) {

        pointService.accumulatePoints(reqDto.getUsername(), reqDto.getAmount(), PointCategory.ADMIN);

        return new RsData<>(
                "200-1",
                "적립 성공",
                new Empty()
        );
    }

    @PutMapping("/deduct")
    public RsData<Empty> deductFromMember(@RequestBody @Validated PointTransferReq reqDto) {

        pointService.deductPoints(reqDto.getUsername(), reqDto.getAmount(), PointCategory.ADMIN);
        return new RsData<>(
                "200-1",
                "차감 성공",
                new Empty()
        );
    }
}
