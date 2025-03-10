package com.NBE3_4_2_Team4.domain.board.question.dto

import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetType
import com.NBE3_4_2_Team4.domain.board.answer.dto.AnswerDto
import com.NBE3_4_2_Team4.domain.board.question.entity.Question

import java.time.LocalDateTime

data class QuestionDto(
    val id: Long,
    val title: String,
    val content: String,
    val name: String,
    val categoryName: String,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
    val recommendCount: Long,
    val answers: List<AnswerDto> = emptyList(),
    val selectedAnswer: AnswerDto? = null,
    val closed: Boolean,
    val amount: Long,
    val assetType: AssetType,
    val authorId: Long
) {
    constructor(question: Question) : this(
        id = question.id,
        title = question.title,
        content = question.content,
        name = question.author.nickname,
        categoryName = question.category.name,
        createdAt = question.createdAt,
        modifiedAt = question.modifiedAt,
        recommendCount = question.recommends.count().toLong(),
        answers = question.answers.map { AnswerDto(it) },
        selectedAnswer = question.selectedAnswer?.let { AnswerDto(it) },
        closed = question.closed,
        amount = question.amount,
        assetType = question.assetType,
        authorId = question.author.id!!
    )
}

