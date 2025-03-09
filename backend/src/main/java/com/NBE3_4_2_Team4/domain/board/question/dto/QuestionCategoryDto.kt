package com.NBE3_4_2_Team4.domain.board.question.dto

import com.NBE3_4_2_Team4.domain.board.question.entity.QuestionCategory

class QuestionCategoryDto(
        val id: Long,
        val name: String
) {
    constructor(category: QuestionCategory) : this(
            id = category.id,
            name = category.name
    )
}
