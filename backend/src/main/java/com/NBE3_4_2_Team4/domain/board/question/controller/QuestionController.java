package com.NBE3_4_2_Team4.domain.board.question.controller;

import com.NBE3_4_2_Team4.domain.board.question.dto.QuestionDto;
import com.NBE3_4_2_Team4.domain.board.question.dto.request.QuestionWriteReqDto;
import com.NBE3_4_2_Team4.domain.board.question.dto.response.QuestionWriteResDto;
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
@Tag(name = "지식인 질문 관리", description = "지식인 질문 관련 API")
@RequestMapping("/api/questions")
public class QuestionController {
    private final QuestionService questionService;

    @GetMapping
    @Operation(summary = "질문 글 조회 with 검색", description = "지식인 질문을 검색어, 페이지, 페이지 크기를 기준으로 조회")
    public PageDto<QuestionDto> getQuestions(@RequestParam(defaultValue = "") String searchKeyword,
                                             @RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int pageSize) {
        return new PageDto<>(
                questionService.findByListed(page, pageSize, searchKeyword)
                        .map(QuestionDto::new)
        );
    }

    @GetMapping("/recommends")
    @Operation(summary = "추천 글 조회", description = "추천 수 기준으로 내림차순 정렬")
    public PageDto<QuestionDto> getRecommended(@RequestParam(defaultValue = "1") int page,
                                               @RequestParam(defaultValue = "10") int pageSize) {
        return new PageDto<>(
                questionService.findByRecommends(page, pageSize)
                        .map(QuestionDto::new)
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "질문 글 단건조회", description = "질문 id에 해당하는 글 조회")
    public QuestionDto getQuestion(@PathVariable long id) {
        Question question = questionService.findById(id).orElseThrow(
                () -> new ServiceException("404-1", "게시글이 존재하지 않습니다.")
        );

        return new QuestionDto(question);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "질문 삭제", description = "질문 id에 해당하는 글 삭제, 작성자만 삭제 가능")
    public RsData<Void> delete(@PathVariable long id) {
        Member actor = AuthManager.getMemberFromContext();
        questionService.delete(id, actor);

        return new RsData<>(
                "200-1",
                "게시글 삭제가 완료되었습니다."
        );
    }

    @PostMapping
    @Operation(summary = "질문 등록")
    public RsData<QuestionWriteResDto> write(@RequestBody @Valid QuestionWriteReqDto reqBody) {
        Member author = AuthManager.getMemberFromContext();
        Question q = questionService.write(reqBody.title(), reqBody.content(), reqBody.categoryId(), author);

        return new RsData<>(
                "200-1",
                "%d번 게시글 생성이 완료되었습니다.".formatted(q.getId()),
                new QuestionWriteResDto(
                        new QuestionDto(q),
                        questionService.count()
                )
        );
    }

    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "질문 수정", description = "질문 id에 해당하는 글 수정, 작성자만 수정 가능")
    public RsData<QuestionDto> update(@PathVariable long id, @RequestBody @Valid QuestionWriteReqDto reqBody) {
        Member actor = AuthManager.getMemberFromContext();
        Question question = questionService.findById(id).orElseThrow(
                () -> new ServiceException("404-1", "게시글이 존재하지 않습니다.")
        );
        questionService.update(question, reqBody.title(), reqBody.content(), actor);

        return new RsData<>(
                "200-1",
                "%d번 게시글 수정이 완료되었습니다.".formatted(id),
                new QuestionDto(question)
        );
    }
}
