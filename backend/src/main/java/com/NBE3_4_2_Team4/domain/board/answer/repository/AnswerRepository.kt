package com.NBE3_4_2_Team4.domain.board.answer.repository

import com.NBE3_4_2_Team4.domain.board.answer.entity.Answer
import com.NBE3_4_2_Team4.domain.board.question.entity.Question
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface AnswerRepository : JpaRepository<Answer, Long> {
    fun findByQuestionAndSelected(question: Question, pageable: Pageable, selected: Boolean): Page<Answer>
}
