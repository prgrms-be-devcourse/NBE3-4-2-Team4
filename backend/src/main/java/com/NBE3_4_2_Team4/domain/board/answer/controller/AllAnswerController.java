package com.NBE3_4_2_Team4.domain.board.answer.controller;

import com.NBE3_4_2_Team4.domain.board.answer.dto.AnswerDto;
import com.NBE3_4_2_Team4.domain.board.answer.entity.Answer;
import com.NBE3_4_2_Team4.domain.board.answer.service.AnswerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@Tag(name = "AllAnswerController", description = "지식인 - 전체 답변 관리 API")
@RequestMapping("/api/answers")
public class AllAnswerController {
    private final AnswerService answerService;

    @Operation(summary = "Get All Answers", description = "모든 답변을 가져옵니다.")
    @GetMapping
    @Transactional(readOnly = true)
    public List<AnswerDto> items() {
        List<Answer> answers = answerService.findAll();

        return answers.stream()
                .map(AnswerDto::new)
                .toList();
    }

    @Operation(summary = "Get Answer by Id", description = "답변 Id를 기준으로 특정 답변을 가져옵니다.")
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public AnswerDto item(@PathVariable long id) {
        Answer answer = answerService.findById(id).orElseThrow(
                () -> new NoSuchElementException("해당 답변은 존재하지 않습니다.")
        );

        return new AnswerDto(answer);
    }
}
