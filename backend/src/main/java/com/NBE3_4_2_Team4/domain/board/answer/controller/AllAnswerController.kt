package com.NBE3_4_2_Team4.domain.board.answer.controller

import com.NBE3_4_2_Team4.domain.board.answer.dto.AnswerDto
import com.NBE3_4_2_Team4.domain.board.answer.dto.AnswerRequestDto
import com.NBE3_4_2_Team4.domain.board.answer.service.AnswerService
import com.NBE3_4_2_Team4.global.rsData.RsData
import com.NBE3_4_2_Team4.standard.base.Empty
import com.NBE3_4_2_Team4.standard.dto.PageDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@Tag(name = "AllAnswerController", description = "지식인 - 전체 답변 관리 API")
@RequestMapping("/api/answers")
class AllAnswerController(
    private val answerService: AnswerService
) {
    @Operation(summary = "답변 전체 다건 조회", description = "모든 답변을 가져옵니다.")
    @GetMapping
    fun items(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") pageSize: Int
    ): PageDto<AnswerDto> {
        return PageDto(
            answerService.itemsAll(page, pageSize)
        )
    }

    @Operation(summary = "답변 단건 조회", description = "답변 Id를 기준으로 특정 답변을 가져옵니다.")
    @GetMapping("/{id}")
    fun item(@PathVariable id: Long): AnswerDto {
        return answerService.item(id)
    }

    @Operation(summary = "답변 수정", description = "답변를 수정합니다.")
    @PatchMapping("/{id}")
    fun modify(
        @RequestBody @Valid answerRequestDto: AnswerRequestDto,
        @PathVariable("id") id: Long
    ): RsData<AnswerDto> {
        return RsData(
            "200-1",
            "${id}번 답변이 수정 되었습니다.",
            answerService.modify(id, answerRequestDto.content)
        )
    }

    @Operation(summary = "답변 삭제", description = "답변을 삭제합니다.")
    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable("id") id: Long
    ): RsData<Empty> {
        answerService.delete(id)

        return RsData(
            "200-1",
            "${id}번 답변이 삭제 되었습니다."
        )
    }
}
