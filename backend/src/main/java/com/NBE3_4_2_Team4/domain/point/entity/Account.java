package com.NBE3_4_2_Team4.domain.point.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long balance;

    @Version
    private Long version;

    //    @ManyToOne(optional = false)
//    @JoinColumn(nullable = false)
//    @JoinColumn
//    private Member member;
}
