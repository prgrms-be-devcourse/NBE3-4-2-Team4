package com.NBE3_4_2_Team4.domain.member.OAuth2RefreshToken.repository;

import com.NBE3_4_2_Team4.domain.member.OAuth2RefreshToken.entity.OAuth2RefreshToken;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OAuth2RefreshTokenRepository extends JpaRepository<OAuth2RefreshToken, Long> {
    Optional<OAuth2RefreshToken> findByMember(Member member);
    void deleteByMember(Member member);
}
