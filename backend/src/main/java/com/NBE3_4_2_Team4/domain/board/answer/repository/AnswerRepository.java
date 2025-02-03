package com.NBE3_4_2_Team4.domain.board.answer.repository;

import com.NBE3_4_2_Team4.domain.board.answer.entity.Answer;
import com.NBE3_4_2_Team4.domain.board.question.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    Optional<Answer> findFirstByOrderByIdDesc();
    List<Answer> findByQuestionOrderByIdDesc(Question question);
    Page<Answer> findByQuestion(Question question, Pageable pageable);
}
