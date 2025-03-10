package com.NBE3_4_2_Team4.domain.asset.point.controller

import com.NBE3_4_2_Team4.domain.asset.point.service.PointService
import com.NBE3_4_2_Team4.global.rsData.RsData
import com.NBE3_4_2_Team4.global.security.AuthManager
import com.NBE3_4_2_Team4.standard.base.Empty
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import lombok.RequiredArgsConstructor
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/points")
@Tag(name = "PointController", description = "API 포인트 컨트롤러")
class PointController (
    private val pointService: PointService,
){

    @PutMapping("/attendance")
    @Operation(summary = "출석요청", description = "출석요청이 이미 완료이면 에러")
    fun attendance(): RsData<Empty> {
        val member = AuthManager.getNonNullMember()
        pointService.attend(member.id)

        return RsData(
            "200-1",
            "출석 성공",
            Empty()
        )
    }
}
