package com.NBE3_4_2_Team4.domain.board.answer.controller;

import com.NBE3_4_2_Team4.domain.board.answer.dto.AnswerDto;
import com.NBE3_4_2_Team4.domain.board.answer.dto.AnswerRequestDto;
import com.NBE3_4_2_Team4.domain.board.answer.service.AnswerService;
import com.NBE3_4_2_Team4.global.rsData.RsData;
import com.NBE3_4_2_Team4.standard.dto.PageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "AnswerController", description = "지식인 - 특정 질문글에 대한 답변 관리 API")
@RequestMapping("/api/questions/{questionId}/answers")
public class AnswerController {
    private final AnswerService answerService;

    @Operation(summary = "답변 등록", description = "질문글에 새로운 답변을 등록합니다.")
    @PostMapping
    public RsData<AnswerDto> write(
            @PathVariable long questionId,
            @RequestBody @Valid AnswerRequestDto answerRequestDto
    ) {
        AnswerDto answerDto = answerService.write(
                questionId,
                answerRequestDto.content()
        );

        return new RsData(
                "201-1",
                "%d번 답변이 등록 되었습니다.".formatted(answerDto.getId()),
                answerDto
        );
    }

    @Operation(summary = "질문 글 내 답변 다건 조회", description = "특정 질문글의 모든 답변을 가져옵니다.")
    @GetMapping
    public PageDto<AnswerDto> items(
            @PathVariable long questionId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return new PageDto<>(
                answerService.items(questionId, page, pageSize)
        );
    }
}
