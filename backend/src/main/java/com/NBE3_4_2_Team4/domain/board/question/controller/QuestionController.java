package com.NBE3_4_2_Team4.domain.board.question.controller;

import com.NBE3_4_2_Team4.domain.board.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;
}
