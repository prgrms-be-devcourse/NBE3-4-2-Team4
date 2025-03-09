package com.NBE3_4_2_Team4.domain.board.question.repository

import com.NBE3_4_2_Team4.domain.board.question.entity.QuestionCategory
import org.springframework.data.jpa.repository.JpaRepository

interface QuestionCategoryRepository : JpaRepository<QuestionCategory, Long>
