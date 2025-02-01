package com.NBE3_4_2_Team4.domain.board.question.controller;

import com.NBE3_4_2_Team4.domain.board.question.dto.QuestionDto;
import com.NBE3_4_2_Team4.domain.board.question.entity.Question;
import com.NBE3_4_2_Team4.domain.board.question.service.QuestionService;
import com.NBE3_4_2_Team4.global.rsData.RsData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;

    @GetMapping
    public List<QuestionDto> getQuestions(@RequestParam(defaultValue = "") String searchKeyword,
                                          @RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "10") int pageSize) {
        return questionService.findByListed(page, pageSize, searchKeyword)
                .stream()
                .map(QuestionDto::new)
                .toList();
    }

    @GetMapping("/{id}")
    public QuestionDto getQuestion(@PathVariable long id) {
        return new QuestionDto(questionService.findById(id));
    }

    @DeleteMapping("/{id}")
    public RsData<Void> delete(@PathVariable long id) {
        questionService.delete(id);
        return new RsData<>(
                "200-1",
                "게시글 삭제가 완료되었습니다."
        );
    }

    record QuestionReqBody(
            @NotNull @Length(min = 2)
            String title,
            @NotNull @Length(min = 2)
            String content,
            @NotNull
            Long categoryId
    ) {}

    record QuestionWriteResBody(
            QuestionDto item,
            long totalCount
    ) {}

    @PostMapping
    public RsData<QuestionWriteResBody> write(@RequestBody @Valid QuestionReqBody reqBody) {
        Question q = questionService.write(reqBody.title, reqBody.content, reqBody.categoryId);
        return new RsData<>(
                "200-1",
                "%d번 게시글 생성이 완료되었습니다.".formatted(q.getId()),
                new QuestionWriteResBody(
                        new QuestionDto(q),
                        questionService.count()
                )
        );
    }

    @PutMapping("/{id}")
    @Transactional
    public RsData<Void> update(@PathVariable long id, @RequestBody @Valid QuestionReqBody reqBody) {
        Question question = questionService.findById(id);
        questionService.update(question, reqBody.title, reqBody.content);

        return new RsData<>(
                "200-1",
                "%d번 게시글 수정이 완료되었습니다.".formatted(id)
        );
    }
}
