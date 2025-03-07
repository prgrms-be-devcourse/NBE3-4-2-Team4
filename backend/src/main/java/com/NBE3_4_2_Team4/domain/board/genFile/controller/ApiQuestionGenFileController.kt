package com.NBE3_4_2_Team4.domain.board.genFile.controller

import com.NBE3_4_2_Team4.domain.base.genFile.controller.ApiGenFileController
import com.NBE3_4_2_Team4.domain.board.genFile.entity.QuestionGenFile
import com.NBE3_4_2_Team4.domain.board.question.entity.Question
import com.NBE3_4_2_Team4.domain.board.question.service.QuestionService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/questions/{parentId}/genFiles")
class ApiQuestionGenFileController(questionService: QuestionService) :
    ApiGenFileController<Question, QuestionGenFile>(questionService) {
    override fun findById(id: Long): Question {
        return (service as QuestionService).findQuestionById(id)
    }
}
