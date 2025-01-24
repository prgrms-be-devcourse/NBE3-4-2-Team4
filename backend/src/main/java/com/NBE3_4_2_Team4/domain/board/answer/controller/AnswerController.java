package com.NBE3_4_2_Team4.domain.board.answer.controller;

import com.NBE3_4_2_Team4.domain.board.answer.dto.AnswerCreateReqBody;
import com.NBE3_4_2_Team4.domain.board.answer.dto.AnswerDto;
import com.NBE3_4_2_Team4.domain.board.answer.entity.Answer;
import com.NBE3_4_2_Team4.domain.board.answer.service.AnswerService;
import com.NBE3_4_2_Team4.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "AnswerController", description = "지식인 - 특정 질문글에 대한 답변 관리 API")
@RequestMapping("/api/questions/{postId}/answers")
public class AnswerController {
    private final AnswerService answerService;

    @Operation(summary = "Write Answer", description = "질문글에 새로운 답변을 등록합니다.")
    @PostMapping
    @Transactional
    public RsData<AnswerDto> write(
            @PathVariable long postId,
            @RequestBody @Valid AnswerCreateReqBody reqBody
    ) {
        Answer answer = answerService.write(reqBody.content());

        return new RsData(
                "201-1",
                "답변이 등록 되었습니다.",
                new AnswerDto(answer)
        );
    }
}
