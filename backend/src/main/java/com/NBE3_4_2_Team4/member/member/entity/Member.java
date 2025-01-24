package com.NBE3_4_2_Team4.member.member.entity;

import com.NBE3_4_2_Team4.member.memberCategory.entity.MemberCategory;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Entity
@EntityListeners({AuditingEntityListener.class})
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn
    @ManyToOne
    private MemberCategory memberCategory;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String phoneNumber;

    private String nickname;

    @CreatedDate
    private LocalDateTime createdAt;
}
