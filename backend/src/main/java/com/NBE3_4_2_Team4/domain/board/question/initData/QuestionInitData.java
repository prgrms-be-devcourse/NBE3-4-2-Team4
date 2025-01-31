package com.NBE3_4_2_Team4.domain.board.question.initData;

import com.NBE3_4_2_Team4.domain.board.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
public class QuestionInitData {
    private final QuestionService questionService;
    @Autowired
    @Lazy
    private QuestionInitData self;

    @Bean
    public ApplicationRunner questionInitDataApplicationRunner() {
        return _ -> self.initData();
    }

    @Transactional
    public void initData() {
        if (questionService.count() > 0) return;

        questionService.createCategory("category1");
        questionService.createCategory("category2");

        questionService.write("title1", "content1", 1L);
        questionService.write("title2", "content2", 1L);
        questionService.write("title3", "content3", 2L);
    }
}
