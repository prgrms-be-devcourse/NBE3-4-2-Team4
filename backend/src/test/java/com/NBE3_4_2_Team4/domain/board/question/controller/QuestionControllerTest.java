package com.NBE3_4_2_Team4.domain.board.question.controller;

import com.NBE3_4_2_Team4.domain.board.question.entity.Question;
import com.NBE3_4_2_Team4.domain.board.question.service.QuestionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class QuestionControllerTest {
    @Autowired
    private QuestionService questionService;

    @Test
    @DisplayName("전체 게시글 조회")
    void t1() {
        List<Question> questions = questionService.findAll();
        assertThat(questions).hasSize(3);
    }

    @Test
    @DisplayName("1번 게시글 조회")
    void t2() {
        Question question = questionService.findById(1L);

        assertThat(question.getId()).isEqualTo(1);
        assertThat(question.getTitle()).isEqualTo("title1");
        assertThat(question.getContent()).isEqualTo("content1");
    }

    @Test
    @DisplayName("게시글 작성")
    void t3() {
        Question question = questionService.write("title4", "content4");

        assertThat(question.getId()).isEqualTo(4);
        assertThat(question.getTitle()).isEqualTo("title4");
        assertThat(question.getContent()).isEqualTo("content4");
    }

    @Test
    @DisplayName("1번 게시글 삭제")
    void t4() {
        questionService.delete(1L);
        List<Question> questions = questionService.findAll();

        assertThat(questions).hasSize(2);
    }

    @Test
    @DisplayName("1번 게시글 수정")
    void t5() {
        Question question = questionService.findById(1L);
        questionService.update(question, "title1 수정", "content1 수정");

        assertThat(question.getId()).isEqualTo(1);
        assertThat(question.getTitle()).isEqualTo("title1 수정");
        assertThat(question.getContent()).isEqualTo("content1 수정");
    }
}
