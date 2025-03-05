package com.NBE3_4_2_Team4.domain.board.question.service;

import com.NBE3_4_2_Team4.domain.board.question.dto.QuestionCategoryDto;
import com.NBE3_4_2_Team4.domain.board.question.entity.QuestionCategory;
import com.NBE3_4_2_Team4.domain.board.question.repository.QuestionCategoryRepository;
import com.NBE3_4_2_Team4.domain.board.question.repository.QuestionRepository;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.global.exceptions.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionCategoryService {
    private final QuestionCategoryRepository questionCategoryRepository;
    private final QuestionRepository questionRepository;

    @Transactional
    public QuestionCategoryDto createCategory(Member actor, String name) {
        QuestionCategory category = new QuestionCategory(name);

        category.checkActorCanCreate(actor);
        questionCategoryRepository.save(category);

        return new QuestionCategoryDto(category);
    }

    @Transactional
    public void deleteCategory(Member actor, long id) {
        QuestionCategory category = questionCategoryRepository.findById(id).orElseThrow();
        category.checkActorCanDelete(actor);

        // 해당 카테고리를 사용중인 질문이 있는지 확인
        if (questionRepository.existsByCategory(category)) {
            throw new ServiceException("400-1", "해당 카테고리를 사용중인 질문이 있습니다.");
        }

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
