package com.NBE3_4_2_Team4.domain.board.genFile.controller

import com.NBE3_4_2_Team4.domain.base.genFile.controller.GenFileController
import com.NBE3_4_2_Team4.domain.board.genFile.entity.QuestionGenFile
import com.NBE3_4_2_Team4.domain.board.question.entity.Question
import com.NBE3_4_2_Team4.domain.board.question.service.QuestionService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/question/genFile")
class QuestionGenFileController(questionService: QuestionService) :
    GenFileController<Question, QuestionGenFile>(questionService) {
    override fun findById(id: Long): Question {
        return (service as QuestionService).findQuestionById(id)
    }

    override fun getGenFileFromParent(parent: Question, fileName: String): QuestionGenFile {
        return parent.genFiles
            .stream()
            .filter { it.fileName == fileName }
            .findFirst()
            .get()
    }
}
