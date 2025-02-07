package com.NBE3_4_2_Team4.global.scheduler;

import com.NBE3_4_2_Team4.domain.board.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuestionClosedScheduler {
    private final QuestionService questionService;

    // 매일 자정에 실행
    @Scheduled(cron = "0 0 0 * * ?")
    // 30초마다 실행
    //@Scheduled(cron = "0/30 * * * * ?")
    public void prePareDelivery() {
        long closedQuestionCount = questionService.closeQuestionsIfExpired();

        log.info(closedQuestionCount + "개의 질문을 만료 처리하였습니다.");
    }
}
