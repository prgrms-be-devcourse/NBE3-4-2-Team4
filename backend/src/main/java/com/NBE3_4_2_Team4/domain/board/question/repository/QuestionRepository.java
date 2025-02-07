package com.NBE3_4_2_Team4.domain.board.question.repository;

import com.NBE3_4_2_Team4.domain.board.question.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long>, QuestionRepositoryCustom {
    Page<Question> findByTitleLike(String title, Pageable pageable);
    Optional<Question> findFirstByOrderByIdDesc();

    @Query("SELECT q FROM Question q WHERE size(q.recommends) > 0 ORDER BY size(q.recommends) DESC")
    Page<Question> findRecommendedQuestions(Pageable pageable);

    List<Question> findByCreatedAtBeforeAndClosed(LocalDateTime expirationDate, boolean closed);
}
