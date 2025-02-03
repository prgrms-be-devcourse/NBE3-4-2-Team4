package com.NBE3_4_2_Team4.domain.board.question.entity;

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
}
