package com.NBE3_4_2_Team4.domain.board.answer.repository;

import com.NBE3_4_2_Team4.domain.board.answer.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    Optional<Answer> findFirstByOrderByIdDesc();
}
