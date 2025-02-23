package com.NBE3_4_2_Team4.domain.board.question.repository;

import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetType;
import com.NBE3_4_2_Team4.domain.board.question.entity.Question;
import com.NBE3_4_2_Team4.domain.board.question.entity.QuestionCategory;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long>, QuestionRepositoryCustom {
    Optional<Question> findFirstByOrderByIdDesc();

    @Query("SELECT q FROM Question q WHERE size(q.recommends) > 0 AND q.rankReceived = false ORDER BY size(q.recommends) DESC")
    Page<Question> findRecommendedQuestions(Pageable pageable);

    @Query("SELECT q FROM Question q WHERE size(q.recommends) > 0 AND q.rankReceived = false ORDER BY size(q.recommends) DESC")
    List<Question> findRecommendedQuestions();

    List<Question> findByCreatedAtBeforeAndClosed(LocalDateTime expirationDate, boolean closed);

    Page<Question> findByCategory(QuestionCategory category, Pageable pageable);

    Page<Question> findByAssetType(AssetType assetType, Pageable pageable);

    Page<Question> findByCategoryAndAssetType(QuestionCategory category, AssetType assetType, Pageable pageable);

    Page<Question> findByAuthor(Member author, Pageable pageable);

    boolean existsByCategory(QuestionCategory category);
}
