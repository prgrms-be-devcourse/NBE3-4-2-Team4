package com.NBE3_4_2_Team4.global.scheduler

import com.NBE3_4_2_Team4.domain.board.question.service.QuestionService
import com.NBE3_4_2_Team4.global.config.AppConfig.Companion.log
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class QuestionClosedScheduler(
    private val questionService: QuestionService
) {
    // 매일 자정에 실행
    @Scheduled(cron = "0 0 0 * * ?") // 30초마다 실행
    //@Scheduled(cron = "0/30 * * * * ?")
    fun prePareDelivery() {
        val closedQuestionCount = questionService.closeQuestionsIfExpired()

        log.info("${closedQuestionCount}개의 질문을 만료 처리하였습니다.")
    }
}
