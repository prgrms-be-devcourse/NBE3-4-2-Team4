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
import com.NBE3_4_2_Team4.standard.dto.PageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "AnswerController", description = "지식인 - 특정 질문글에 대한 답변 관리 API")
@RequestMapping("/api/questions/{questionId}/answers")
public class AnswerController {
    private final AnswerService answerService;
    private final QuestionService questionService;

    @Operation(summary = "답변 등록", description = "질문글에 새로운 답변을 등록합니다.")
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

    @Operation(summary = "질문 글 내 답변 다건 조회", description = "특정 질문글의 모든 답변을 가져옵니다.")
    @GetMapping
    @Transactional
    public PageDto<AnswerDto> items(
            @PathVariable long questionId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        Question question = questionService.findById(questionId).orElseThrow(
                () -> new ServiceException("404-1", "해당 질문글이 존재하지 않습니다.")
        );

        return new PageDto<>(
                answerService.findByQuestion(question, page, pageSize)
                        .map(AnswerDto::new)
        );
    }
}
