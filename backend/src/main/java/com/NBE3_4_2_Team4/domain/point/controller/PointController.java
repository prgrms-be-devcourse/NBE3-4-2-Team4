package com.NBE3_4_2_Team4.domain.point.controller;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.point.dto.PointHistoryResponse;
import com.NBE3_4_2_Team4.domain.point.dto.PointTransferReqDto;
import com.NBE3_4_2_Team4.domain.point.entity.PointCategory;
import com.NBE3_4_2_Team4.domain.point.service.PointService;
import com.NBE3_4_2_Team4.global.rsData.RsData;
import com.NBE3_4_2_Team4.standard.base.Empty;
import com.NBE3_4_2_Team4.standard.dto.PageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.NBE3_4_2_Team4.global.security.AuthManager.getMemberFromContext;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/points")
public class PointController {
    private final PointService pointService;
    private final static int POINT_HISTORY_SIZE = 10;

    @PutMapping("/transfer")
    public RsData<Empty> transfer(@RequestBody @Validated PointTransferReqDto reqDto) {
        Member sender = getMemberFromContext();
        if (sender == null) throw new RuntimeException("로그인 후 이용해주세요");

        pointService.transfer(sender.getUsername(), reqDto.getUsername(), reqDto.getAmount(), PointCategory.TRANSFER);
        return new RsData<>(
                "200-1",
                "송금 성공",
                new Empty()
        );
    }

    @GetMapping
    public RsData<PageDto<PointHistoryResponse>> getPointHistories(@RequestParam(defaultValue = "0") int page) {
        Member member = getMemberFromContext();

        if (member == null) throw new RuntimeException("로그인 후 이용해주세요");

        PageDto<PointHistoryResponse> points = pointService.getHistoryPage(member, page, POINT_HISTORY_SIZE);

        return new RsData<>(
                "200-1",
                "OK",
                points
        );
    }
}
