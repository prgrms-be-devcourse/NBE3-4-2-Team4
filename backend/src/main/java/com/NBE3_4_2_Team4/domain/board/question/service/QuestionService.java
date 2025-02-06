package com.NBE3_4_2_Team4.domain.board.question.service;

import com.NBE3_4_2_Team4.domain.board.question.entity.Question;
import com.NBE3_4_2_Team4.domain.board.question.entity.QuestionCategory;
import com.NBE3_4_2_Team4.domain.board.question.repository.QuestionCategoryRepository;
import com.NBE3_4_2_Team4.domain.board.question.repository.QuestionRepository;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.global.exceptions.ServiceException;
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

    public Question write(String title, String content, Long categoryId, Member author) {
        QuestionCategory category = questionCategoryRepository.findById(categoryId).orElseThrow();

        return questionRepository.save(Question.builder()
                .title(title)
                .content(content)
                .author(author)
                .category(category)
                .build());
    }

    public List<Question> findAll() {
        return questionRepository.findAll();
    }

    public Optional<Question> findLatest() {
        return questionRepository.findFirstByOrderByIdDesc();
    }

    public Page<Question> findByListed(int page, int pageSize) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        return questionRepository.findAll(pageRequest);
    }

    public Page<Question> findByListed(int page, int pageSize, String searchKeyword) {
        if (searchKeyword == null) return findByListed(page, pageSize);

        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        searchKeyword = "%" + searchKeyword + "%";

        return questionRepository.findByTitleLike(searchKeyword, pageRequest);
    }

    public Page<Question> findByRecommends(int page, int pageSize) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        return questionRepository.findRecommendedQuestions(pageRequest);
    }

    public Optional<Question> findById(long id) {
        return questionRepository.findById(id);
    }

    public void delete(long id, Member actor) {
        Question question = questionRepository.findById(id).orElseThrow(
                () -> new ServiceException("404-1", "게시글이 존재하지 않습니다.")
        );
        if (!question.getAuthor().equals(actor)) {
            throw new ServiceException("403-1", "게시글 작성자만 삭제할 수 있습니다.");
        }
        questionRepository.delete(question);
    }

    public void update(Question q, String title, String content, Member actor) {
        if (!q.getAuthor().equals(actor)) {
            throw new ServiceException("403-1", "게시글 작성자만 수정할 수 있습니다.");
        }
        q.setTitle(title);
        q.setContent(content);
    }
}
