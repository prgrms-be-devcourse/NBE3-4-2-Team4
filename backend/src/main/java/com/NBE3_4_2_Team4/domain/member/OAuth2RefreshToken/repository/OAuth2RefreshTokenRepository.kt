package com.NBE3_4_2_Team4.domain.member.OAuth2RefreshToken.repository

import com.NBE3_4_2_Team4.domain.member.OAuth2RefreshToken.entity.OAuth2RefreshToken
import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository

interface OAuth2RefreshTokenRepository: JpaRepository<OAuth2RefreshToken, Long> {
    fun findByMember(member: Member): OAuth2RefreshToken?
}