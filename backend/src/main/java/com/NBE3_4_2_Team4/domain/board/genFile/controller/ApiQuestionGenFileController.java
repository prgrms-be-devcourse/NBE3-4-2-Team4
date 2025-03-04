package com.NBE3_4_2_Team4.domain.board.genFile.controller;


import com.NBE3_4_2_Team4.domain.base.genFile.controller.ApiGenFileController;
import com.NBE3_4_2_Team4.domain.board.genFile.entity.QuestionGenFile;
import com.NBE3_4_2_Team4.domain.board.question.entity.Question;
import com.NBE3_4_2_Team4.domain.board.question.service.QuestionService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/questions/{parentId}/genFiles")
public class ApiQuestionGenFileController extends ApiGenFileController<Question, QuestionGenFile> {
    public ApiQuestionGenFileController(QuestionService questionService) {
        super(questionService);
    }

    @Override
    protected Question findById(long id) {
        return ((QuestionService) service).findQuestionById(id);
    }
}
