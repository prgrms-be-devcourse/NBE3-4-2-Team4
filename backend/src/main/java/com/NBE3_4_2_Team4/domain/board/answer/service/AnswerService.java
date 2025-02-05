package com.NBE3_4_2_Team4.domain.board.answer.service;

import com.NBE3_4_2_Team4.domain.board.answer.entity.Answer;
import com.NBE3_4_2_Team4.domain.board.answer.repository.AnswerRepository;
import com.NBE3_4_2_Team4.domain.board.question.entity.Question;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.global.exceptions.ServiceException;
import com.NBE3_4_2_Team4.global.security.AuthManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnswerService {
    private final AnswerRepository answerRepository;

    public Answer write(Question question, Member author, String content) {
        if(author.getId() == question.getAuthor().getId())
            throw new ServiceException("400-1", "작성자는 답변을 등록할 수 없습니다.");

        Answer answer = Answer
                .builder()
                .question(question)
                .author(author)
                .content(content)
                .build();

        return answerRepository.save(answer);
    }

    public long count() {
        return answerRepository.count();
    }

    public Optional<Answer> findLatest() {
        return answerRepository.findFirstByOrderByIdDesc();
    }

    public Answer findById(long id) {
        Answer answer = answerRepository.findById(id).orElseThrow(
                () -> new ServiceException("404-2", "해당 답변은 존재하지 않습니다.")
        );

        return answer;
    }

    public List<Answer> findAll() {
        return answerRepository.findAll();
    }

    public Page<Answer> findAll(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Order.desc("id")));

        return answerRepository.findAll(pageable);
    }

    public void checkActorCanModify(Answer answer, Member actor) {
        if (actor == null) throw new ServiceException("401-1", "로그인 후 이용해주세요.");

        if (actor.equals(answer.getAuthor())) return;

        throw new ServiceException("403-2", "작성자만 답변을 수정할 수 있습니다.");
    }

    public void checkActorCanDelete(Answer answer, Member actor) {
        if (actor == null) throw new ServiceException("401-1", "로그인 후 이용해주세요.");

        if (actor.getRole() == Member.Role.ADMIN) return;

        if (actor.equals(answer.getAuthor())) return;

        throw new ServiceException("403-2", "작성자만 답변을 삭제할 수 있습니다.");
    }

    public Answer modify(long id, String content) {
        Answer answer = findById(id);

        Member actor = AuthManager.getMemberFromContext();

        checkActorCanModify(answer, actor);

        answer.setContent(content);

        return answer;
    }

    public void delete(long id) {
        Answer answer = findById(id);

        Member actor = AuthManager.getMemberFromContext();

        checkActorCanDelete(answer, actor);

        answerRepository.delete(answer);
    }

    public List<Answer> findByQuestionOrderByIdDesc(Question question) {
        return answerRepository.findByQuestionOrderByIdDesc(question);
    }

    public Page<Answer> findByQuestion(Question question, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Order.desc("id")));

        return answerRepository.findByQuestion(question, pageable);
    }
}
