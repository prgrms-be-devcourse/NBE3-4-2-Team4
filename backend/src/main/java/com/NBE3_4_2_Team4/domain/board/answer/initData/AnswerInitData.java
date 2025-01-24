package com.NBE3_4_2_Team4.domain.board.answer.initData;

import com.NBE3_4_2_Team4.domain.board.answer.entity.Answer;
import com.NBE3_4_2_Team4.domain.board.answer.service.AnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
public class AnswerInitData {
    private final AnswerService answerService;
    @Autowired
    @Lazy
    private AnswerInitData self;

    @Bean
    public ApplicationRunner answerInitDataApplicationRunner() {
        return args -> {
            self.work1();
        };
    }

    @Transactional
    public void work1() {
        if (answerService.count() > 0) return;

        Answer answer1 = answerService.write("답변 내용1");
        Answer answer2 = answerService.write("답변 내용2");
        Answer answer3 = answerService.write("답변 내용3");
    }
}
