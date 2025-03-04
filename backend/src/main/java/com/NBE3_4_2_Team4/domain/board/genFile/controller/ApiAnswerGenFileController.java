package com.NBE3_4_2_Team4.domain.board.genFile.controller;


import com.NBE3_4_2_Team4.domain.base.genFile.controller.ApiGenFileController;
import com.NBE3_4_2_Team4.domain.board.answer.entity.Answer;
import com.NBE3_4_2_Team4.domain.board.answer.service.AnswerService;
import com.NBE3_4_2_Team4.domain.board.genFile.entity.AnswerGenFile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/answers/{parentId}/genFiles")
public class ApiAnswerGenFileController extends ApiGenFileController<Answer, AnswerGenFile> {
    public ApiAnswerGenFileController (AnswerService answerService) {
        super(answerService);
    }

    @Override
    protected Answer findById(long id) {
        return ((AnswerService) service).findById(id);
    }
}
