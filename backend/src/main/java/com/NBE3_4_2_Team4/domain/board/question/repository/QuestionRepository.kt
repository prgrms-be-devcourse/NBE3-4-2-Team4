package com.NBE3_4_2_Team4.domain.board.question.repository

import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetType
import com.NBE3_4_2_Team4.domain.board.question.entity.Question
import com.NBE3_4_2_Team4.domain.board.question.entity.QuestionCategory
import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

import java.time.LocalDateTime
import java.util.Optional

interface QuestionRepository : JpaRepository<Question, Long>, QuestionRepositoryCustom {
    fun findFirstByOrderByIdDesc(): Optional<Question>

    @Query("SELECT q FROM Question q WHERE size(q.recommends) > 0 AND q.rankReceived = false ORDER BY size(q.recommends) DESC")
    fun findRecommendedQuestions(pageable: Pageable): Page<Question>

    @Query("SELECT q FROM Question q WHERE size(q.recommends) > 0 AND q.rankReceived = false ORDER BY size(q.recommends) DESC")
    fun findRecommendedQuestions(): List<Question>

    fun findByCreatedAtBeforeAndClosed(expirationDate: LocalDateTime, closed: Boolean): List<Question>

    fun findByCategory(category: QuestionCategory, pageable: Pageable): Page<Question>

    fun findByAssetType(assetType: AssetType, pageable: Pageable): Page<Question>

    fun findByCategoryAndAssetType(category: QuestionCategory, assetType: AssetType, pageable: Pageable): Page<Question>

    fun findByAuthor(author: Member, pageable: Pageable): Page<Question>

    fun existsByCategory(category: QuestionCategory): Boolean
}
