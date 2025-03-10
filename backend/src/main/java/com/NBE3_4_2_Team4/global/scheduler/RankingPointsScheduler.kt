package com.NBE3_4_2_Team4.global.scheduler

import com.NBE3_4_2_Team4.domain.board.question.service.QuestionService
import com.NBE3_4_2_Team4.global.config.AppConfig.Companion.log
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class RankingPointsScheduler(
    private val questionService: QuestionService
) {
    // 매일 자정에 실행
    @Scheduled(cron = "0 0 0 * * ?")
    fun updateRankingPoints() {
        log.info("랭킹 포인트 지급 스케줄러 실행")
        questionService.awardRankingPoints()
    }
}
