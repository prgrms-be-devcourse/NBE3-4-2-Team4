package com.NBE3_4_2_Team4.domain.point.controller;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.point.dto.PointHistoryReq;
import com.NBE3_4_2_Team4.domain.point.dto.PointHistoryRes;
import com.NBE3_4_2_Team4.domain.point.dto.PointTransferReq;
import com.NBE3_4_2_Team4.domain.point.entity.PointCategory;
import com.NBE3_4_2_Team4.domain.point.service.PointHistoryService;
import com.NBE3_4_2_Team4.domain.point.service.PointService;
import com.NBE3_4_2_Team4.global.rsData.RsData;
import com.NBE3_4_2_Team4.standard.base.Empty;
import com.NBE3_4_2_Team4.standard.dto.PageDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.NBE3_4_2_Team4.global.security.AuthManager.getMemberFromContext;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/points")
public class PointController {
    private final PointService pointService;
    private final PointHistoryService pointHistoryService;
    private final static int POINT_HISTORY_SIZE = 10;

    @PutMapping("/transfer")
    public RsData<Empty> transfer(@Valid @RequestBody PointTransferReq reqDto) {
        Member sender = getMemberFromContext();
        if (sender == null) throw new AuthenticationCredentialsNotFoundException("로그인이 필요합니다.");
        ;

        pointService.transfer(sender.getUsername(), reqDto.getUsername(), reqDto.getAmount(), PointCategory.TRANSFER);

        return new RsData<>(
                "200-1",
                "송금 성공",
                new Empty()
        );
    }

    @GetMapping("/all")
    public RsData<PageDto<PointHistoryRes>> getPointHistoriesWithDateAndCategory(@RequestParam(defaultValue = "1") int page) {
        Member member = getMemberFromContext();
        if (member == null) throw new AuthenticationCredentialsNotFoundException("로그인이 필요합니다.");


        PageDto<PointHistoryRes> points = pointHistoryService.getHistoryPage(member, page, POINT_HISTORY_SIZE);

        return new RsData<>(
                "200-1",
                "OK",
                points
        );
    }

    @GetMapping()
    public RsData<PageDto<PointHistoryRes>> getPointHistories(@Valid @ModelAttribute PointHistoryReq pointHistoryReq) {
        Member member = getMemberFromContext();
        //if (member == null) throw new AuthenticationCredentialsNotFoundException("로그인이 필요합니다.");


        PageDto<PointHistoryRes> points =
                pointHistoryService.getHistoryPageWithFilter(member, POINT_HISTORY_SIZE, pointHistoryReq);

        return new RsData<>(
                "200-1",
                "OK",
                points
        );
    }

}
