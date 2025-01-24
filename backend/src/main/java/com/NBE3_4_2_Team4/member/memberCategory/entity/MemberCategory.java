package com.NBE3_4_2_Team4.member.memberCategory.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class MemberCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
}
