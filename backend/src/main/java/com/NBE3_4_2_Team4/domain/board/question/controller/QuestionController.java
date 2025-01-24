package com.NBE3_4_2_Team4.domain.board.question.controller;

import com.NBE3_4_2_Team4.domain.board.question.entity.Question;
import com.NBE3_4_2_Team4.domain.board.question.service.QuestionService;
import com.NBE3_4_2_Team4.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;

    @GetMapping
    public List<Question> getQuestions() {
        return questionService.findAll();
    }

    @GetMapping("/{id}")
    public Question getQuestion(@PathVariable long id) {
        return questionService.findById(id);
    }

    @DeleteMapping("/{id}")
    public RsData<Void> delete(@PathVariable long id) {
        questionService.delete(id);
        return new RsData<>(
                "200-1",
                "게시글 삭제가 완료되었습니다."
        );
    }
}
