package com.NBE3_4_2_Team4.domain.member.OAuth2RefreshToken.service

import com.NBE3_4_2_Team4.domain.member.OAuth2RefreshToken.entity.OAuth2RefreshToken
import com.NBE3_4_2_Team4.domain.member.OAuth2RefreshToken.repository.OAuth2RefreshTokenRepository
import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class OAuth2RefreshTokenService(
    val oAuth2RefreshTokenRepository: OAuth2RefreshTokenRepository
) {
    fun saveOrUpdateOAuth2RefreshToken(member: Member, refreshToken: String?, oAuth2Id: String) {
        if (!refreshToken.isNullOrBlank()) {
            val oAuth2RefreshToken: OAuth2RefreshToken? = oAuth2RefreshTokenRepository.findByMember(member)
            if (oAuth2RefreshToken != null) {
                // 이미 존재하는 경우 업데이트
                oAuth2RefreshToken.refreshToken =  refreshToken
                oAuth2RefreshTokenRepository.save(oAuth2RefreshToken) // 업데이트
            } else {
                // 없으면 새로 저장
                oAuth2RefreshTokenRepository.save(
                    OAuth2RefreshToken(
                        member = member,
                        oAuth2Id = oAuth2Id,
                        refreshToken = refreshToken
                    )
                )
            }
        }
    }
}