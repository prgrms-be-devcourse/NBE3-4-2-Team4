package com.NBE3_4_2_Team4.global.security.user.customUser

import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import org.springframework.security.core.userdetails.User
import org.springframework.security.oauth2.core.user.OAuth2User

class CustomUser(private val member: Member) :
    User(member.username, member.password, member.getAuthorities()),
    OAuth2User {
    override fun getAttributes(): Map<String, Any> {
        return java.util.Map.of()
    }

    override fun getName(): String {
        return username
    }
}