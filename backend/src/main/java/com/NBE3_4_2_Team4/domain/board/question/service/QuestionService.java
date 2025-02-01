package com.NBE3_4_2_Team4.domain.board.question.service;

import com.NBE3_4_2_Team4.domain.board.question.entity.Question;
import com.NBE3_4_2_Team4.domain.board.question.entity.QuestionCategory;
import com.NBE3_4_2_Team4.domain.board.question.repository.QuestionCategoryRepository;
import com.NBE3_4_2_Team4.domain.board.question.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Question write(String title, String content, Long categoryId) {
        return questionRepository.save(Question.builder()
                .title(title)
                .content(content)
                .categoryId(categoryId)
                .build());
    }

    public List<Question> findAll() {
        return questionRepository.findAll();
    }

    public Optional<Question> findLatest() {
        return questionRepository.findFirstByOrderByIdDesc();
    }

    public List<Question> findByListed(int page, int pageSize) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        Page<Question> postPage = questionRepository.findAll(pageRequest);

        return postPage.getContent();
    }

    public List<Question> findByListed(int page, int pageSize, String searchKeyword) {
        if (searchKeyword == null) return findByListed(page, pageSize);

        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        searchKeyword = "%" + searchKeyword + "%";

        return questionRepository.findByTitleLike(searchKeyword, pageRequest).getContent();
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
