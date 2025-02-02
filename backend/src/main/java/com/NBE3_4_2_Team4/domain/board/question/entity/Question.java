package com.NBE3_4_2_Team4.domain.board.question.entity;

import com.NBE3_4_2_Team4.domain.board.answer.entity.Answer;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.global.jpa.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Question extends BaseTime {
    @ManyToOne
    private Member author;

    @Column(length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    private QuestionCategory category;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL) // 질문 삭제 시 답변 삭제
    private List<Answer> answers;
}
