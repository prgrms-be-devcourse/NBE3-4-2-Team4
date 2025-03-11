package com.NBE3_4_2_Team4.global.security

import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.global.security.user.customUser.CustomUser
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component

@Component
class AuthManager {
    fun setLogin(member: Member) {
        val userDetails: UserDetails = CustomUser(member)

        val authentication: Authentication = UsernamePasswordAuthenticationToken(
            userDetails,
            userDetails.password,
            userDetails.authorities
        )

        SecurityContextHolder.getContext().authentication = authentication
    }

    companion object{
        fun getMemberFromContext(): Member? {
            val authentication = SecurityContextHolder.getContext().authentication
            return if (authentication != null && authentication.principal is CustomUser) {
                (authentication.principal as CustomUser).member
            } else {
                null
            }
        }

        fun getNonNullMember(): Member {
            val member = getMemberFromContext() ?: throw AuthenticationCredentialsNotFoundException("로그인이 필요합니다.")
            return member
        }
    }
}