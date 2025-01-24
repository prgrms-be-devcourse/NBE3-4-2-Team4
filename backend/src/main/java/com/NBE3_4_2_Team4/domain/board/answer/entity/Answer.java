package com.NBE3_4_2_Team4.domain.board.answer.entity;

import com.NBE3_4_2_Team4.global.jpa.entity.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Answer extends BaseTime {
    @Column(columnDefinition = "TEXT")
    String content;
}
