package com.NBE3_4_2_Team4.domain.board.question.initData;

import com.NBE3_4_2_Team4.domain.board.question.service.QuestionService;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository;
import com.NBE3_4_2_Team4.domain.member.member.service.MemberService;
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
    private final MemberRepository memberRepository;

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

        Member admin = memberRepository.findByUsername("admin@test.com").orElseThrow();
        questionService.createCategory("category1");
        questionService.createCategory("category2");

        for (int i = 1; i <= 20; i++) {
            questionService.write("title" + i, "content" + i, (long)i % 2 + 1, admin);
        }
    }
}
