package com.NBE3_4_2_Team4.domain.board.recommend.service;

import com.NBE3_4_2_Team4.domain.board.question.entity.Question;
import com.NBE3_4_2_Team4.domain.board.question.repository.QuestionRepository;
import com.NBE3_4_2_Team4.domain.board.recommend.entity.Recommend;
import com.NBE3_4_2_Team4.domain.board.recommend.repository.RecommendRepository;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.global.exceptions.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class RecommendService {
    private final QuestionRepository questionRepository;
    private final RecommendRepository recommendRepository;

    @Transactional
    public boolean toggleRecommend(long questionId, Member member) {
        Question question = questionRepository.findById(questionId).orElseThrow(
                () -> new ServiceException("404-1", "게시글이 존재하지 않습니다.")
        );

        // 추천 여부 확인
        boolean isAlreadyRecommended = recommendRepository.existsByQuestionAndMember(question, member);

        if (isAlreadyRecommended) { // 추천 취소
            cancelRecommend(questionId, member);
            return false;
        } else { // 추천 추가
            recommend(questionId, member);
            return true;
        }
    }

    @Transactional
    public void recommend(long questionId, Member member) {
        Question question = questionRepository.findById(questionId).orElseThrow(
                () -> new ServiceException("404-1", "게시글이 존재하지 않습니다.")
        );

        if (recommendRepository.existsByQuestionAndMember(question, member)) { // 중복 추천 방지
            throw new ServiceException("400-1", "이미 추천한 게시글입니다.");
        }
        if (question.getAuthor().equals(member)) { // 본인 글 추천 방지
            throw new ServiceException("400-2", "자신의 게시글은 추천할 수 없습니다.");
        }

        Recommend recommend = Recommend.builder()
                .question(question)
                .member(member)
                .build();

        recommendRepository.save(recommend);
    }

    @Transactional
    public void cancelRecommend(long questionId, Member member) {
        Question question = questionRepository.findById(questionId).orElseThrow(
                () -> new ServiceException("404-1", "게시글이 존재하지 않습니다.")
        );

        Recommend recommend = recommendRepository.findByQuestionAndMember(question, member)
                .orElseThrow(NoSuchElementException::new);
        recommendRepository.delete(recommend);
    }
}
