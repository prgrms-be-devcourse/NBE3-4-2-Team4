package com.NBE3_4_2_Team4.domain.member.OAuth2RefreshToken.entity

import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import jakarta.persistence.*


@Entity
class OAuth2RefreshToken(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @OneToOne(fetch = FetchType.EAGER)
    private val member: Member,

    private val oAuth2Id: String,

    private var refreshToken: String
) {
}