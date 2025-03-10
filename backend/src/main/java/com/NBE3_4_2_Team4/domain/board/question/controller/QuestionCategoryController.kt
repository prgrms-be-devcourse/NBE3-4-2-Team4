package com.NBE3_4_2_Team4.domain.board.question.controller

import com.NBE3_4_2_Team4.domain.board.question.dto.QuestionCategoryDto
import com.NBE3_4_2_Team4.domain.board.question.dto.request.QuestionCategoryReqDto
import com.NBE3_4_2_Team4.domain.board.question.service.QuestionCategoryService
import com.NBE3_4_2_Team4.global.rsData.RsData
import com.NBE3_4_2_Team4.global.security.AuthManager
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RestController
@Tag(name = "질문 카테고리 관리", description = "지식인 카테고리 관련 API")
@RequestMapping("/api/questions/categories")
class QuestionCategoryController(
    private val questionCategoryService: QuestionCategoryService
) {
    @GetMapping
    @Operation(summary = "카테고리 조회", description = "카테고리 목록 가져오기")
    fun getCategories(): List<QuestionCategoryDto> {
        return questionCategoryService.getCategories()
    }

    @PostMapping
    @Operation(summary = "카테고리 추가", description = "카테고리 추가하기")
    fun createCategory(@RequestBody reqBody: QuestionCategoryReqDto): RsData<QuestionCategoryDto> {
        val actor = AuthManager.getNonNullMember()
        return RsData(
            "201-1",
            "카테고리 추가 성공",
            questionCategoryService.createCategory(actor, reqBody.name)
        )
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "카테고리 삭제", description = "카테고리 삭제하기")
    fun deleteCategory(@PathVariable id: Long): RsData<Void> {
        val actor = AuthManager.getNonNullMember()
        questionCategoryService.deleteCategory(actor, id)

        return RsData(
            "200-1",
            "카테고리 삭제 성공"
        )
    }
}
