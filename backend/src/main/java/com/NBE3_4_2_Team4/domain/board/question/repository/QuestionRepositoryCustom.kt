package com.NBE3_4_2_Team4.domain.board.question.repository

import com.NBE3_4_2_Team4.domain.board.question.entity.Question
import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.standard.search.QuestionSearchKeywordType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface QuestionRepositoryCustom {
    fun findByKw(kwType: QuestionSearchKeywordType, kw: String, pageable: Pageable): Page<Question>
    fun findByAnswerAuthor(author: Member, pageable: Pageable): Page<Question>
}
