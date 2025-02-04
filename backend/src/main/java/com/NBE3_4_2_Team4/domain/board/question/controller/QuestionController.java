package com.NBE3_4_2_Team4.domain.board.question.controller;

import com.NBE3_4_2_Team4.domain.board.question.dto.QuestionDto;
import com.NBE3_4_2_Team4.domain.board.question.dto.request.QuestionWriteReqDto;
import com.NBE3_4_2_Team4.domain.board.question.dto.response.QuestionWriteResDto;
import com.NBE3_4_2_Team4.domain.board.question.entity.Question;
import com.NBE3_4_2_Team4.domain.board.question.service.QuestionService;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.global.exceptions.QuestionNotFoundException;
import com.NBE3_4_2_Team4.global.rsData.RsData;
import com.NBE3_4_2_Team4.global.security.AuthManager;
import com.NBE3_4_2_Team4.standard.dto.PageDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;

    @GetMapping
    public PageDto<QuestionDto> getQuestions(@RequestParam(defaultValue = "") String searchKeyword,
                                             @RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int pageSize) {
        return new PageDto<>(
                questionService.findByListed(page, pageSize, searchKeyword)
                        .map(QuestionDto::new)
        );
    }

    @GetMapping("/{id}")
    public QuestionDto getQuestion(@PathVariable long id) {
        Question question = questionService.findById(id).orElseThrow(QuestionNotFoundException::new);

        return new QuestionDto(question);
    }

    @DeleteMapping("/{id}")
    public RsData<Void> delete(@PathVariable long id) {
        if (!questionService.findById(id).isPresent()) {
            throw new QuestionNotFoundException();
        }
        questionService.delete(id);
        return new RsData<>(
                "200-1",
                "게시글 삭제가 완료되었습니다."
        );
    }

    @PostMapping
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
    public RsData<QuestionDto> update(@PathVariable long id, @RequestBody @Valid QuestionWriteReqDto reqBody) {
        Question question = questionService.findById(id).orElseThrow(QuestionNotFoundException::new);

        questionService.update(question, reqBody.title(), reqBody.content());

        return new RsData<>(
                "200-1",
                "%d번 게시글 수정이 완료되었습니다.".formatted(id),
                new QuestionDto(question)
        );
    }
}
