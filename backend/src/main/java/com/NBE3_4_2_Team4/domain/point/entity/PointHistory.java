package com.NBE3_4_2_Team4.domain.point.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class PointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private String correlationId;  // 연관 그룹 ID

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;


    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Account account;

    @ManyToOne
    @JoinColumn
    private Account counterAccount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PointCategory pointCategory;
}
