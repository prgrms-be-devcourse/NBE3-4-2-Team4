package com.NBE3_4_2_Team4.domain.board.answer.service;

import com.NBE3_4_2_Team4.domain.board.answer.entity.Answer;
import com.NBE3_4_2_Team4.domain.board.answer.repository.AnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnswerService {
    private final AnswerRepository answerRepository;

    public Answer write(String content) {
        Answer answer = Answer
                .builder()
                .content(content)
                .build();

        return answerRepository.save(answer);
    }
}
