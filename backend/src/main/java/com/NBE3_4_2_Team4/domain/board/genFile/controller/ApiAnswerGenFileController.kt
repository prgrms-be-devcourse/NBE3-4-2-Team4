package com.NBE3_4_2_Team4.domain.board.genFile.controller

import com.NBE3_4_2_Team4.domain.base.genFile.controller.ApiGenFileController
import com.NBE3_4_2_Team4.domain.board.answer.entity.Answer
import com.NBE3_4_2_Team4.domain.board.answer.service.AnswerService
import com.NBE3_4_2_Team4.domain.board.genFile.entity.AnswerGenFile
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/answers/{parentId}/genFiles")
class ApiAnswerGenFileController(answerService: AnswerService) :
    ApiGenFileController<Answer, AnswerGenFile>(answerService) {
    override fun findById(id: Long): Answer {
        return (service as AnswerService).findById(id)
    }
}
