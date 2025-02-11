package com.NBE3_4_2_Team4.domain.board.answer.controller;

import com.NBE3_4_2_Team4.domain.board.answer.dto.AnswerDto;
import com.NBE3_4_2_Team4.domain.board.answer.dto.AnswerRequestDto;
import com.NBE3_4_2_Team4.domain.board.answer.service.AnswerService;
import com.NBE3_4_2_Team4.global.rsData.RsData;
import com.NBE3_4_2_Team4.standard.base.Empty;
import com.NBE3_4_2_Team4.standard.dto.PageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "AllAnswerController", description = "지식인 - 전체 답변 관리 API")
@RequestMapping("/api/answers")
public class AllAnswerController {
    private final AnswerService answerService;

    @Operation(summary = "답변 전체 다건 조회", description = "모든 답변을 가져옵니다.")
    @GetMapping
    public PageDto<AnswerDto> items(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return new PageDto<>(
                answerService.itemsAll(page, pageSize)
        );
    }

    @Operation(summary = "답변 단건 조회", description = "답변 Id를 기준으로 특정 답변을 가져옵니다.")
    @GetMapping("/{id}")
    public AnswerDto item(@PathVariable long id) {
        return answerService.item(id);
    }

    @Operation(summary = "답변 수정", description = "답변를 수정합니다.")
    @PatchMapping("/{id}")
    public RsData<AnswerDto> modify(
            @RequestBody @Valid AnswerRequestDto answerRequestDto,
            @PathVariable("id") long id
    ) {
        return new RsData<>(
                "200-1",
                "%d번 답변이 수정 되었습니다.".formatted(id),
                answerService.modify(id, answerRequestDto.content())
        );
    }

    @Operation(summary = "답변 삭제", description = "답변을 삭제합니다.")
    @DeleteMapping("/{id}")
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
