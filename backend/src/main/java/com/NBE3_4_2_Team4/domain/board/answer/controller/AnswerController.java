package com.NBE3_4_2_Team4.domain.board.answer.controller;

import com.NBE3_4_2_Team4.domain.board.answer.dto.AnswerDto;
import com.NBE3_4_2_Team4.domain.board.answer.dto.AnswerRequestDto;
import com.NBE3_4_2_Team4.domain.board.answer.entity.Answer;
import com.NBE3_4_2_Team4.domain.board.answer.service.AnswerService;
import com.NBE3_4_2_Team4.domain.board.question.entity.Question;
import com.NBE3_4_2_Team4.domain.board.question.service.QuestionService;
import com.NBE3_4_2_Team4.global.rsData.RsData;
import com.NBE3_4_2_Team4.standard.base.Empty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@Tag(name = "AnswerController", description = "지식인 - 특정 질문글에 대한 답변 관리 API")
@RequestMapping("/api/questions/{questionId}/answers")
public class AnswerController {
    private final AnswerService answerService;
    private final QuestionService questionService;

    @ModelAttribute("question")
    public Question getQuestion(@PathVariable long questionId) {
        return questionService.findById(questionId).get();
    }

    @Operation(summary = "Write Answer", description = "질문글에 새로운 답변을 등록합니다.")
    @PostMapping
    @Transactional
    public RsData<AnswerDto> write(
            @ModelAttribute("question") Question question,
            @RequestBody @Valid AnswerRequestDto answerRequestDto
    ) {
        Answer answer = answerService.write(
                question,
                answerRequestDto.content()
        );

        return new RsData(
                "201-1",
                "%d번 답변이 등록 되었습니다.".formatted(answer.getId()),
                new AnswerDto(answer)
        );
    }

    @Operation(summary = "Update Answer", description = "답변를 수정합니다.")
    @PutMapping("/{id}")
    @Transactional
    public RsData<AnswerDto> modify(
            @ModelAttribute("question") Question question,
            @RequestBody @Valid AnswerRequestDto answerRequestDto,
            @PathVariable("id") long id
    ) {
        Answer answer = answerService.findById(id).orElseThrow(
                () -> new NoSuchElementException("해당 답변은 존재하지 않습니다.")
        );

        answerService.modify(answer, answerRequestDto.content());

        return new RsData<>(
                "200-1",
                "%d번 답변이 수정 되었습니다.".formatted(answer.getId()),
                new AnswerDto(answer)
        );
    }

    @Operation(summary = "Delete Answer", description = "답변을 삭제합니다.")
    @DeleteMapping("/{id}")
    @Transactional
    public RsData<Empty> delete(
            @ModelAttribute("question") Question question,
            @PathVariable("id") long id
    ) {
        Answer answer = answerService.findById(id).orElseThrow(
                () -> new NoSuchElementException("해당 답변은 존재하지 않습니다.")
        );

        answerService.delete(answer);

        return new RsData<>(
                "200-1",
                "%d번 답변이 삭제 되었습니다.".formatted(answer.getId())
        );
    }
}
