package com.NBE3_4_2_Team4.domain.board.question.service;

import com.NBE3_4_2_Team4.domain.board.question.entity.Question;
import com.NBE3_4_2_Team4.domain.board.question.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;

    public long count() {
        return questionRepository.count();
    }

    public Question write(String title, String content) {
        return questionRepository.save(Question.builder()
                .title(title)
                .content(content)
                .build());
    }
}
