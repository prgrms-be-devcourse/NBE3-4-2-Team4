package com.NBE3_4_2_Team4.domain.board.question.repository;

import com.NBE3_4_2_Team4.domain.board.question.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    Page<Question> findByTitleLike(String title, Pageable pageable);
    Optional<Question> findFirstByOrderByIdDesc();
}
