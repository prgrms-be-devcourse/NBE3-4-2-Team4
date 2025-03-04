package com.NBE3_4_2_Team4.domain.board.genFile.controller;

import com.NBE3_4_2_Team4.domain.base.genFile.controller.GenFileController;
import com.NBE3_4_2_Team4.domain.board.answer.entity.Answer;
import com.NBE3_4_2_Team4.domain.board.answer.service.AnswerService;
import com.NBE3_4_2_Team4.domain.board.genFile.entity.AnswerGenFile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/answer/genFile")
public class AnswerGenFileController extends GenFileController<Answer, AnswerGenFile> {
    public AnswerGenFileController (AnswerService answerService) {
        super(answerService);
    }

    @Override
    protected Answer findById(long id) {
        return ((AnswerService) service).findById(id);
    }

    @Override
    protected AnswerGenFile getGenFileFromParent(Answer parent, String fileName) {
        return parent.getGenFiles()
                .stream()
                .filter(f -> f.getFileName().equals(fileName))
                .findFirst()
                .get();
    }
}
