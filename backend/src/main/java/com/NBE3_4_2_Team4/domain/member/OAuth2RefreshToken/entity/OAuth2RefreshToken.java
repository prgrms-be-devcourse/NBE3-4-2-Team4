package com.NBE3_4_2_Team4.domain.member.OAuth2RefreshToken.entity;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Member member;

    @Setter
    private String refreshToken;
}
