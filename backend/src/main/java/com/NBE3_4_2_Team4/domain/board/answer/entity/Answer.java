package com.NBE3_4_2_Team4.domain.board.answer.entity;

import com.NBE3_4_2_Team4.domain.board.question.entity.Question;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.global.exceptions.ServiceException;
import com.NBE3_4_2_Team4.global.jpa.entity.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Answer extends BaseTime {
    @ManyToOne(fetch = FetchType.LAZY)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member author;

    @Column(columnDefinition = "TEXT")
    private String content;

    private boolean selected;

    private LocalDateTime selectedAt;

    public void checkActorCanModify(Member actor) {
        if (!actor.equals(this.getAuthor()))
            throw new ServiceException("403-2", "작성자만 답변을 수정할 수 있습니다.");
    }

    public void checkActorCanDelete(Member actor) {
        if (actor.getRole() != Member.Role.ADMIN && !actor.equals(this.getAuthor()))
            throw new ServiceException("403-2", "작성자만 답변을 삭제할 수 있습니다.");
    }

    public void modify(String content) {
        this.content = content;
    }

    public void select() {
        this.selected = true;
        this.setSelectedAt();
    }

    public void setSelectedAt() {
        this.selectedAt = LocalDateTime.now();
    }

    public void setAuthor(Member author) {
        this.author = author;
    }
}
