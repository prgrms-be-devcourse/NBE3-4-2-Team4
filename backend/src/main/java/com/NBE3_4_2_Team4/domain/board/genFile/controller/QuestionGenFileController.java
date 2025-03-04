package com.NBE3_4_2_Team4.domain.board.genFile.controller;

import com.NBE3_4_2_Team4.domain.base.genFile.controller.GenFileController;
import com.NBE3_4_2_Team4.domain.board.genFile.entity.QuestionGenFile;
import com.NBE3_4_2_Team4.domain.board.question.entity.Question;
import com.NBE3_4_2_Team4.domain.board.question.service.QuestionService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/question/genFile")
public class QuestionGenFileController extends GenFileController<Question, QuestionGenFile> {
    public QuestionGenFileController(QuestionService questionService) {
        super(questionService);
    }

    @Override
    protected Question findById(long id) {
        return ((QuestionService) service).findQuestionById(id);
    }

    @Override
    protected QuestionGenFile getGenFileFromParent(Question parent, String fileName) {
        return parent.getGenFiles()
                .stream()
                .filter(f -> f.getFileName().equals(fileName))
                .findFirst()
                .get();
    }
}
