package com.NBE3_4_2_Team4.domain.board.question.entity;

import com.NBE3_4_2_Team4.domain.board.answer.entity.Answer;
import com.NBE3_4_2_Team4.domain.board.recommend.entity.Recommend;
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

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<Recommend> recommends;

    @OneToOne
    private Answer selectedAnswer;

    private boolean closed; //질문 상태(답변 추가 가능 여부)

    private long point;

    private boolean rankReceived; // 랭킹 포인트 지급 여부

    public long getRecommendCount() { // 추천 수 반환
        return recommends == null ? 0 : recommends.size();
    }

    public boolean isSelectedAnswer(Answer answer) { // 채택된 답변인지 확인
        return selectedAnswer != null && selectedAnswer.equals(answer);
    }
}
