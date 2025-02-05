package com.NBE3_4_2_Team4.domain.board.question.initData;

import com.NBE3_4_2_Team4.domain.board.question.service.QuestionService;
import com.NBE3_4_2_Team4.domain.board.recommend.service.RecommendService;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.member.initData.MemberInitData;
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

@Order(1)
@Configuration
@RequiredArgsConstructor
public class QuestionInitData {
    private final QuestionService questionService;
    private final MemberRepository memberRepository;
    private final MemberInitData memberInitData;
    private final RecommendService recommendService;

    @Autowired
    @Lazy
    private QuestionInitData self;

    @Bean
    public ApplicationRunner questionInitDataApplicationRunner() {
        return _ -> {
            memberInitData.work(); // admin이 먼저 생성되도록 하기 위해 호출
            self.initData();
        };
    }

    @Transactional
    public void initData() {
        if (questionService.count() > 0) return;

        Member admin = memberRepository.findByUsername("admin@test.com").orElseThrow();
        Member testUser = memberRepository.findByUsername("test@test.com").orElseThrow();

        questionService.createCategory("category1");
        questionService.createCategory("category2");

        for (int i = 1; i <= 20; i++) {
            Member author = (i >= 10) ? testUser : admin;
            questionService.write("title" + i, "content" + i, (long)i % 2 + 1, author);
        }

        recommendService.recommend(1L, testUser);
        recommendService.recommend(11L, admin);
    }
}
