package com.NBE3_4_2_Team4.domain.board.question.controller;

import com.NBE3_4_2_Team4.domain.board.question.dto.QuestionCategoryDto;
import com.NBE3_4_2_Team4.domain.board.question.dto.request.QuestionCategoryReqDto;
import com.NBE3_4_2_Team4.domain.board.question.service.QuestionCategoryService;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.global.rsData.RsData;
import com.NBE3_4_2_Team4.global.security.AuthManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "질문 카테고리 관리", description = "지식인 카테고리 관련 API")
@RequestMapping("/api/questions/categories")
public class QuestionCategoryController {
    private final QuestionCategoryService questionCategoryService;

    @GetMapping
    @Operation(summary = "카테고리 조회", description = "카테고리 목록 가져오기")
    public List<QuestionCategoryDto> getCategories() {
        return questionCategoryService.getCategories();
    }

    @PostMapping
    @Operation(summary = "카테고리 추가", description = "카테고리 추가하기")
    public RsData<QuestionCategoryDto> createCategory(@RequestBody QuestionCategoryReqDto reqBody) {
        Member actor = AuthManager.getMemberFromContext();
        return new RsData<>(
            "201-1",
            "카테고리 추가 성공",
            questionCategoryService.createCategory(actor, reqBody.name())
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "카테고리 삭제", description = "카테고리 삭제하기")
    public RsData<Void> deleteCategory(@PathVariable long id) {
        Member actor = AuthManager.getMemberFromContext();
        questionCategoryService.deleteCategory(actor, id);

        return new RsData<>(
            "200-1",
            "카테고리 삭제 성공"
        );
    }
}
