package com.NBE3_4_2_Team4.domain.board.answer.service;

import com.NBE3_4_2_Team4.domain.board.answer.entity.Answer;
import com.NBE3_4_2_Team4.domain.board.answer.repository.AnswerRepository;
import com.NBE3_4_2_Team4.domain.board.question.entity.Question;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnswerService {
    private final AnswerRepository answerRepository;

    public Answer write(Question question, Member author, String content) {
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

    public Optional<Answer> findById(long id) {
        return answerRepository.findById(id);
    }

    public List<Answer> findAll() {
        return answerRepository.findAll();
    }

    public Answer modify(Answer answer, String content) {
        answer.setContent(content);

        return answer;
    }

    public void delete(Answer answer) {
        answerRepository.delete(answer);
    }

    public List<Answer> findByQuestionOrderByIdDesc(Question question) {
        return answerRepository.findByQuestionOrderByIdDesc(question);
    }
}
