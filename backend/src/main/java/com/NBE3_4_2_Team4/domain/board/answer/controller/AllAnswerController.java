package com.NBE3_4_2_Team4.domain.board.answer.controller;

import com.NBE3_4_2_Team4.domain.board.answer.dto.AnswerDto;
import com.NBE3_4_2_Team4.domain.board.answer.dto.AnswerRequestDto;
import com.NBE3_4_2_Team4.domain.board.answer.entity.Answer;
import com.NBE3_4_2_Team4.domain.board.answer.service.AnswerService;
import com.NBE3_4_2_Team4.global.rsData.RsData;
import com.NBE3_4_2_Team4.standard.base.Empty;
import com.NBE3_4_2_Team4.standard.dto.PageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "AllAnswerController", description = "지식인 - 전체 답변 관리 API")
@RequestMapping("/api/answers")
public class AllAnswerController {
    private final AnswerService answerService;

    @Operation(summary = "답변 전체 다건 조회", description = "모든 답변을 가져옵니다.")
    @GetMapping
    @Transactional(readOnly = true)
    public PageDto<AnswerDto> items(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return new PageDto<>(
                answerService.findAll(page, pageSize)
                .map(AnswerDto::new)
        );
    }

    @Operation(summary = "답변 단건 조회", description = "답변 Id를 기준으로 특정 답변을 가져옵니다.")
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public AnswerDto item(@PathVariable long id) {
        Answer answer = answerService.findById(id);

        return new AnswerDto(answer);
    }

    @Operation(summary = "답변 수정", description = "답변를 수정합니다.")
    @PatchMapping("/{id}")
    @Transactional
    public RsData<AnswerDto> modify(
            @RequestBody @Valid AnswerRequestDto answerRequestDto,
            @PathVariable("id") long id
    ) {
        Answer answer = answerService.modify(id, answerRequestDto.content());

        return new RsData<>(
                "200-1",
                "%d번 답변이 수정 되었습니다.".formatted(answer.getId()),
                new AnswerDto(answer)
        );
    }

    @Operation(summary = "답변 삭제", description = "답변을 삭제합니다.")
    @DeleteMapping("/{id}")
    @Transactional
    public RsData<Empty> delete(
            @PathVariable("id") long id
    ) {
        answerService.delete(id);

        return new RsData<>(
                "200-1",
                "%d번 답변이 삭제 되었습니다.".formatted(id)
        );
    }
}
