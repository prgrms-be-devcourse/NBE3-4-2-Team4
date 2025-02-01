package com.NBE3_4_2_Team4.domain.point.controller;

import com.NBE3_4_2_Team4.domain.point.dto.PointHistoryResponse;
import com.NBE3_4_2_Team4.domain.point.service.PointService;
import com.NBE3_4_2_Team4.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/points")
public class PointController {
    private final PointService pointService;
    private final static int POINT_HISTORY_SIZE = 10;

    @GetMapping
    public RsData<Page<PointHistoryResponse>> getPointHistories(@RequestParam(defaultValue = "0") int page) {
        long accountId = 1;

        Page<PointHistoryResponse> points = pointService.getHistoryPage(accountId, page, POINT_HISTORY_SIZE);

        return new RsData<>(
                "200-1",
                "OK",
                points
        );
    }
}
