package com.NBE3_4_2_Team4.domain.board.answer.initData;

import com.NBE3_4_2_Team4.domain.board.answer.entity.Answer;
import com.NBE3_4_2_Team4.domain.board.answer.service.AnswerService;
import com.NBE3_4_2_Team4.domain.board.question.entity.Question;
import com.NBE3_4_2_Team4.domain.board.question.initData.QuestionInitData;
import com.NBE3_4_2_Team4.domain.board.question.service.QuestionService;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.member.initData.MemberInitData;
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

@Order(2)
@Configuration
@RequiredArgsConstructor
@DependsOn("questionInitData")
public class AnswerInitData {
    private final AnswerService answerService;
    private final QuestionService questionService;
    private final QuestionInitData questionInitData;
    private final MemberRepository memberRepository;
    private final MemberInitData memberInitData;

    @Value("${custom.initData.member.member1.username}")
    private String member1Username;

    @Autowired
    @Lazy
    private AnswerInitData self;

    @Bean
    public ApplicationRunner answerInitDataApplicationRunner() {
        return args -> {
            memberInitData.work();
            questionInitData.initData();
            self.work();
        };
    }

    @Transactional
    public void work() {
        if (answerService.count() > 0) return;

        Question question1 = questionService.findById(1).get();
        Question question2 = questionService.findById(2).get();

        Member author = memberRepository.findByUsername(member1Username).get();

        Answer answer1 = answerService.save(question1, author, "답변 내용1");
        Answer answer2 = answerService.save(question1, author, "답변 내용2");
        Answer answer3 = answerService.save(question2, author, "답변 내용3");
    }
}
