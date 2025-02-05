package com.NBE3_4_2_Team4.domain.board.question.service;

import com.NBE3_4_2_Team4.domain.board.answer.entity.Answer;
import com.NBE3_4_2_Team4.domain.board.answer.service.AnswerService;
import com.NBE3_4_2_Team4.domain.board.question.entity.Question;
import com.NBE3_4_2_Team4.domain.board.question.entity.QuestionCategory;
import com.NBE3_4_2_Team4.domain.board.question.repository.QuestionCategoryRepository;
import com.NBE3_4_2_Team4.domain.board.question.repository.QuestionRepository;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.global.exceptions.ServiceException;
import com.NBE3_4_2_Team4.domain.point.entity.PointCategory;
import com.NBE3_4_2_Team4.domain.point.service.PointService;
import com.NBE3_4_2_Team4.global.security.AuthManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final QuestionCategoryRepository questionCategoryRepository;
    private final PointService pointService;
    private final AnswerService answerService;

    public QuestionCategory createCategory(String name) {
        return questionCategoryRepository.save(QuestionCategory.builder()
                .name(name)
                .build());
    }

    public long count() {
        return questionRepository.count();
    }

    public Question write(String title, String content, Long categoryId, Member author, long point) {
        QuestionCategory category = questionCategoryRepository.findById(categoryId).orElseThrow();
        Question question = Question.builder()
                .title(title)
                .content(content)
                .author(author)
                .category(category)
                .point(point)
                .build();

        //질문글 작성 시 포인트 차감
        pointService.deductPoints(author.getUsername(), point, PointCategory.QUESTION);

        return questionRepository.save(question);
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

    public Question select(long id, long answerId) {
        Question question = findById(id).orElseThrow(
                () -> new ServiceException("404-1", "게시글이 존재하지 않습니다.")
        );
        Answer answer = answerService.findById(answerId);
        Member actor = AuthManager.getMemberFromContext();

        if(question.isClosed())
            throw new ServiceException("400-1", "이미 채택이 완료된 질문입니다.");

        if(actor == null)
            throw new ServiceException("401-1", "로그인 후 이용해주세요.");

        if(!actor.equals(question.getAuthor()))
            throw new ServiceException("403-2", "작성자만 답변을 채택할 수 있습니다.");

        if(question.getId() != answer.getQuestion().getId())
            throw new ServiceException("403-3", "해당 질문글 내의 답변만 채택할 수 있습니다.");

        question.setSelectedAnswer(answer);
        question.setClosed(true);
        answer.setSelected(true);
        answer.setSelectedAt(LocalDateTime.now());

        //질문글 채택 시 채택된 답변 작성자 포인트 지급
        pointService.accumulatePoints(answer.getAuthor().getUsername(), question.getPoint(), PointCategory.ANSWER);

        return question;
    }
}
