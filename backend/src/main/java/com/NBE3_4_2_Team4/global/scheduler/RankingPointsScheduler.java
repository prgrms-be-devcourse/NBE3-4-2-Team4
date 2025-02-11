package com.NBE3_4_2_Team4.global.scheduler;

import com.NBE3_4_2_Team4.domain.board.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankingPointsScheduler {
    private final QuestionService questionService;

    // 매일 자정에 실행
    @Scheduled(cron = "0 0 0 * * ?")
    public void updateRankingPoints() {
        log.info("랭킹 포인트 지급 스케줄러 실행");
        questionService.awardRankingPoints();
    }
}
