package com.NBE3_4_2_Team4.domain.board.answer.controller;

import com.NBE3_4_2_Team4.domain.board.answer.dto.AnswerDto;
import com.NBE3_4_2_Team4.domain.board.answer.dto.AnswerRequestDto;
import com.NBE3_4_2_Team4.domain.board.answer.entity.Answer;
import com.NBE3_4_2_Team4.domain.board.answer.service.AnswerService;
import com.NBE3_4_2_Team4.domain.board.question.entity.Question;
import com.NBE3_4_2_Team4.domain.board.question.service.QuestionService;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.global.exceptions.ServiceException;
import com.NBE3_4_2_Team4.global.rsData.RsData;
import com.NBE3_4_2_Team4.global.security.AuthManager;
import com.NBE3_4_2_Team4.standard.base.Empty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "AnswerController", description = "지식인 - 특정 질문글에 대한 답변 관리 API")
@RequestMapping("/api/questions/{questionId}/answers")
public class AnswerController {
    private final AnswerService answerService;
    private final QuestionService questionService;

    @Operation(summary = "Write Answer", description = "질문글에 새로운 답변을 등록합니다.")
    @PostMapping
    @Transactional
    public RsData<AnswerDto> write(
            @PathVariable long questionId,
            @RequestBody @Valid AnswerRequestDto answerRequestDto
    ) {
        Question question = questionService.findById(questionId).orElseThrow(
                () -> new ServiceException("404-1", "해당 질문글이 존재하지 않습니다.")
        );

        Member actor = AuthManager.getMemberFromContext();

        Answer answer = answerService.write(
                question,
                actor,
                answerRequestDto.content()
        );

        return new RsData(
                "201-1",
                "%d번 답변이 등록 되었습니다.".formatted(answer.getId()),
                new AnswerDto(answer)
        );
    }

    @Operation(summary = "Update Answer", description = "답변를 수정합니다.")
    @PatchMapping("/{id}")
    @Transactional
    public RsData<AnswerDto> modify(
            @PathVariable long questionId,
            @RequestBody @Valid AnswerRequestDto answerRequestDto,
            @PathVariable("id") long id
    ) {
        Answer answer = answerService.findById(id).orElseThrow(
                () -> new ServiceException("404-2", "해당 답변은 존재하지 않습니다.")
        );

        Member actor = AuthManager.getMemberFromContext();

        answer.checkActorCanModify(actor);

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
            @PathVariable long questionId,
            @PathVariable("id") long id
    ) {
        Answer answer = answerService.findById(id).orElseThrow(
                () -> new ServiceException("404-2", "해당 답변은 존재하지 않습니다.")
        );

        Member actor = AuthManager.getMemberFromContext();

        answer.checkActorCanDelete(actor);

        answerService.delete(answer);

        return new RsData<>(
                "200-1",
                "%d번 답변이 삭제 되었습니다.".formatted(answer.getId())
        );
    }

    @Operation(summary = "Get All Answers by Question Id", description = "특정 질문글의 모든 답변을 가져옵니다.")
    @GetMapping
    @Transactional
    public List<AnswerDto> items(
            @PathVariable long questionId
    ) {
        Question question = questionService.findById(questionId).orElseThrow(
                () -> new ServiceException("404-1", "해당 질문글이 존재하지 않습니다.")
        );

        List<Answer> answers = answerService.findByQuestionOrderByIdDesc(question);

        return answers.stream()
                .map(AnswerDto::new)
                .toList();
    }
}
