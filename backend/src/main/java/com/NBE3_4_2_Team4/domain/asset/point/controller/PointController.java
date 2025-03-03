package com.NBE3_4_2_Team4.domain.asset.point.controller;


import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.asset.main.service.AssetHistoryService;
import com.NBE3_4_2_Team4.domain.asset.point.service.PointService;
import com.NBE3_4_2_Team4.global.rsData.RsData;
import com.NBE3_4_2_Team4.standard.base.Empty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.NBE3_4_2_Team4.global.security.AuthManager.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/points")
@Tag(name = "PointController", description = "API 포인트 컨트롤러")
public class PointController {
    private final PointService pointService;
    private final AssetHistoryService assetHistoryService;
    private final static int POINT_HISTORY_SIZE = 10;

    @PutMapping("/attendance")
    @Operation(summary="출석요청", description="출석요청이 이미 완료이면 에러")
    public RsData<Empty> attendance() {
        Member member = getNonNullMember();
        pointService.attend(member.getId());

        return new RsData<>(
                "200-1",
                "출석 성공",
                new Empty()
        );
    }
}
