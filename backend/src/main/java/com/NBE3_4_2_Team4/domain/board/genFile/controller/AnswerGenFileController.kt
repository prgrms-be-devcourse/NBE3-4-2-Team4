package com.NBE3_4_2_Team4.domain.board.genFile.controller

import com.NBE3_4_2_Team4.domain.base.genFile.controller.GenFileController
import com.NBE3_4_2_Team4.domain.board.answer.entity.Answer
import com.NBE3_4_2_Team4.domain.board.answer.service.AnswerService
import com.NBE3_4_2_Team4.domain.board.genFile.entity.AnswerGenFile
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/answer/genFile")
class AnswerGenFileController(answerService: AnswerService) :
    GenFileController<Answer, AnswerGenFile>(answerService) {
    override fun findById(id: Long): Answer {
        return (service as AnswerService).findById(id)
    }

    override fun getGenFileFromParent(parent: Answer, fileName: String): AnswerGenFile {
        return parent.genFiles
            .stream()
            .filter { it.fileName == fileName }
            .findFirst()
            .get()
    }
}
