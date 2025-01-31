package com.NBE3_4_2_Team4.domain.board.question.service;

import com.NBE3_4_2_Team4.domain.board.question.entity.Question;
import com.NBE3_4_2_Team4.domain.board.question.entity.QuestionCategory;
import com.NBE3_4_2_Team4.domain.board.question.repository.QuestionCategoryRepository;
import com.NBE3_4_2_Team4.domain.board.question.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final QuestionCategoryRepository questionCategoryRepository;

    public QuestionCategory createCategory(String name) {
        return questionCategoryRepository.save(QuestionCategory.builder()
                .name(name)
                .build());
    }

    public long count() {
        return questionRepository.count();
    }

    public Question write(String title, String content) {
        return questionRepository.save(Question.builder()
                .title(title)
                .content(content)
                .build());
    }

    public List<Question> findAll() {
        return questionRepository.findAll();
    }

    public Question findById(long id) {
        return questionRepository.findById(id).orElseThrow();
    }

    public void delete(long id) {
        questionRepository.deleteById(id);
    }

    public void update(Question q, String title, String content) {
        q.setTitle(title);
        q.setContent(content);
    }
}
