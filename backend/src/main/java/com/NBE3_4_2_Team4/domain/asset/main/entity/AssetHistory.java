package com.NBE3_4_2_Team4.domain.asset.main.entity;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
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
public class AssetHistory {

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

    @ManyToOne
    private Member member;

    @ManyToOne
    @JoinColumn
    private Member counterMember;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssetCategory assetCategory;

    //Cash or Point
    @Column(nullable = false)
    private AssetType assetType;
}
