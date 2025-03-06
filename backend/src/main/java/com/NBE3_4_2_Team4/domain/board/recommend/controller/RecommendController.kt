package com.NBE3_4_2_Team4.domain.board.recommend.controller

import com.NBE3_4_2_Team4.domain.board.recommend.service.RecommendService
import com.NBE3_4_2_Team4.global.rsData.RsData
import com.NBE3_4_2_Team4.global.security.AuthManager
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "질문 추천 관리", description = "게시글 추천 관리 API")
@RequestMapping("/api/questions/{questionId}/recommend")
class RecommendController(
    private val recommendService: RecommendService
) {
    @PutMapping
    @Operation(summary = "게시글 추천", description = "추천/취소 토글, 중복 추천 불가, 본인 글 추천 불가")
    fun recommend(@PathVariable questionId: Long): RsData<Void> {
        val member = AuthManager.getMemberFromContext()
        val isRecommended = recommendService.toggleRecommend(questionId, member)

        return if (isRecommended) {
            RsData(
                "200-1",
                "게시글 추천이 완료되었습니다."
            )
        } else {
            RsData(
                "200-2",
                "게시글 추천을 취소하였습니다."
            )
        }
    }
}
