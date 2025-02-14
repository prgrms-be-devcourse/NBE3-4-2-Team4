package com.NBE3_4_2_Team4.domain.board.question.service;

import com.NBE3_4_2_Team4.domain.board.question.dto.QuestionCategoryDto;
import com.NBE3_4_2_Team4.domain.board.question.entity.QuestionCategory;
import com.NBE3_4_2_Team4.domain.board.question.repository.QuestionCategoryRepository;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionCategoryService {
    private final QuestionCategoryRepository questionCategoryRepository;

    @Transactional
    public QuestionCategoryDto createCategory(Member actor, String name) {
        QuestionCategory category = QuestionCategory.builder()
                .name(name)
                .build();

        category.checkActorCanCreate(actor);
        questionCategoryRepository.save(category);

        return new QuestionCategoryDto(category);
    }

    @Transactional
    public void deleteCategory(Member actor, long id) {
        QuestionCategory category = questionCategoryRepository.findById(id).orElseThrow();
        category.checkActorCanDelete(actor);

        questionCategoryRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<QuestionCategoryDto> getCategories() {
        return questionCategoryRepository.findAll()
                .stream()
                .map(QuestionCategoryDto::new)
                .toList();
    }
}
