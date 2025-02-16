package com.NBE3_4_2_Team4.domain.board.answer.service;

import com.NBE3_4_2_Team4.domain.board.answer.dto.AnswerDto;
import com.NBE3_4_2_Team4.domain.board.answer.entity.Answer;
import com.NBE3_4_2_Team4.domain.board.answer.repository.AnswerRepository;
import com.NBE3_4_2_Team4.domain.board.question.entity.Question;
import com.NBE3_4_2_Team4.domain.board.question.repository.QuestionRepository;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.global.exceptions.ServiceException;
import com.NBE3_4_2_Team4.global.security.AuthManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;

    @Transactional
    public AnswerDto write(long questionId, String content) {
        Question question = questionRepository.findById(questionId).orElseThrow(
                () -> new ServiceException("404-1", "해당 질문글이 존재하지 않습니다.")
        );

        Member actor = AuthManager.getNonNullMember();

        return new AnswerDto(save(question, actor, content));
    }

    public Answer save(Question question, Member author, String content) {
        if(author.getId() == question.getAuthor().getId())
            throw new ServiceException("400-1", "작성자는 답변을 등록할 수 없습니다.");

        return answerRepository.save(
                Answer
                        .builder()
                        .question(question)
                        .author(author)
                        .content(content)
                        .build());
    }

    public long count() {
        return answerRepository.count();
    }

    public Answer findById(long id) {
        Answer answer = answerRepository.findById(id).orElseThrow(
                () -> new ServiceException("404-2", "해당 답변은 존재하지 않습니다.")
        );

        return answer;
    }

    @Transactional(readOnly = true)
    public AnswerDto item(long id) {
        return new AnswerDto(findById(id));
    }

    @Transactional(readOnly = true)
    public Page<AnswerDto> itemsAll(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Order.desc("id")));

        return answerRepository.findAll(pageable)
                .map(AnswerDto::new);
    }

    @Transactional(readOnly = true)
    public Page<AnswerDto> items(long questionId, int page, int pageSize) {
        Question question = questionRepository.findById(questionId).orElseThrow(
                () -> new ServiceException("404-1", "해당 질문글이 존재하지 않습니다.")
        );

        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Order.desc("id")));

        return answerRepository.findByQuestionAndSelected(question, pageable, false)
                .map(AnswerDto::new);
    }

    @Transactional
    public AnswerDto modify(long id, String content) {
        Answer answer = findById(id);
        Member actor = AuthManager.getNonNullMember();

        answer.checkActorCanModify(actor);
        answer.modify(content);

        return new AnswerDto(answer);
    }

    @Transactional
    public void delete(long id) {
        Answer answer = findById(id);
        Member actor = AuthManager.getNonNullMember();

        answer.checkActorCanDelete(actor);

        answerRepository.delete(answer);
    }

    public void flush() {
        answerRepository.flush();
    }
}
