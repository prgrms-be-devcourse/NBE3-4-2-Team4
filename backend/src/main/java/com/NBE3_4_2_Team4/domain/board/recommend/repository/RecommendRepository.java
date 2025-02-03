package com.NBE3_4_2_Team4.domain.board.recommend.repository;

import com.NBE3_4_2_Team4.domain.board.question.entity.Question;
import com.NBE3_4_2_Team4.domain.board.recommend.entity.Recommend;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendRepository extends JpaRepository<Recommend, Long> {
    boolean existsByQuestionAndMember(Question question, Member member);
}
