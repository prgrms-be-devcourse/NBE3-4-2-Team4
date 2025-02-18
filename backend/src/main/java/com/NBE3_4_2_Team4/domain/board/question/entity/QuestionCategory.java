package com.NBE3_4_2_Team4.domain.board.question.entity;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.global.exceptions.ServiceException;
import com.NBE3_4_2_Team4.global.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionCategory extends BaseEntity {
    @Column(length = 100)
    private String name;

    public void checkActorCanCreate(Member actor) {
        if (actor.getRole() != Member.Role.ADMIN)
            throw new ServiceException("403-1", "관리자만 카테고리를 관리할 수 있습니다.");
    }

    public void checkActorCanDelete(Member actor) {
        if (actor.getRole() != Member.Role.ADMIN)
            throw new ServiceException("403-2", "관리자만 카테고리를 관리할 수 있습니다.");
    }
}
