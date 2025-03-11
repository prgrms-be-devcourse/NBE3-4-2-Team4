package com.NBE3_4_2_Team4.domain.board.answer.dto

import com.NBE3_4_2_Team4.domain.board.answer.entity.Answer
import java.time.LocalDateTime

class AnswerDto(
    val id: Long,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
    val questionId: Long,
    val authorId: Long,
    val authorName: String,
    val content: String,
    val selected: Boolean,
    val selectedAt: LocalDateTime?

) {
    constructor(answer: Answer) : this (
        id = answer.id,
        createdAt = answer.createdAt,
        modifiedAt = answer.modifiedAt,
        questionId = answer.question.id,
        authorId = answer.author.id!!,
        authorName = answer.author.nickname,
        content = answer.content,
        selected = answer.selected,
        selectedAt = answer.selectedAt
    )
}
