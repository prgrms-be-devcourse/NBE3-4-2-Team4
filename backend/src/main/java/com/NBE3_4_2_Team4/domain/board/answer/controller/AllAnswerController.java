package com.NBE3_4_2_Team4.domain.board.answer.controller;

import com.NBE3_4_2_Team4.domain.board.answer.dto.AnswerDto;
import com.NBE3_4_2_Team4.domain.board.answer.dto.AnswerRequestDto;
import com.NBE3_4_2_Team4.domain.board.answer.entity.Answer;
import com.NBE3_4_2_Team4.domain.board.answer.service.AnswerService;
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
                () -> new ServiceException("404-2", "해당 답변은 존재하지 않습니다.")
        );

        return new AnswerDto(answer);
    }

    @Operation(summary = "Update Answer", description = "답변를 수정합니다.")
    @PatchMapping("/{id}")
    @Transactional
    public RsData<AnswerDto> modify(
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
}
