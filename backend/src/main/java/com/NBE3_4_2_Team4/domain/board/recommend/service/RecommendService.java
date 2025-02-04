package com.NBE3_4_2_Team4.domain.board.recommend.service;

import com.NBE3_4_2_Team4.domain.board.question.entity.Question;
import com.NBE3_4_2_Team4.domain.board.question.repository.QuestionRepository;
import com.NBE3_4_2_Team4.domain.board.recommend.entity.Recommend;
import com.NBE3_4_2_Team4.domain.board.recommend.repository.RecommendRepository;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository;
import com.NBE3_4_2_Team4.global.exceptions.QuestionNotFoundException;
import com.NBE3_4_2_Team4.global.exceptions.RecommendAlreadyException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class RecommendService {
    private final QuestionRepository questionRepository;
    private final RecommendRepository recommendRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void recommend(long questionId, long memberId) {
        Question question = questionRepository.findById(questionId).orElseThrow(QuestionNotFoundException::new);
        Member member = memberRepository.findById(memberId).orElseThrow();

        if (recommendRepository.existsByQuestionAndMember(question, member)) { // 중복 추천 방지
            throw new RecommendAlreadyException();
        }
        Recommend recommend = Recommend.builder()
                .question(question)
                .member(member)
                .build();

        recommendRepository.save(recommend);
    }

    @Transactional
    public void cancelRecommend(long questionId, long memberId) {
        Question question = questionRepository.findById(questionId).orElseThrow(QuestionNotFoundException::new);
        Member member = memberRepository.findById(memberId).orElseThrow();

        Recommend recommend = recommendRepository.findByQuestionAndMember(question, member)
                .orElseThrow(NoSuchElementException::new);
        recommendRepository.delete(recommend);
    }
}
