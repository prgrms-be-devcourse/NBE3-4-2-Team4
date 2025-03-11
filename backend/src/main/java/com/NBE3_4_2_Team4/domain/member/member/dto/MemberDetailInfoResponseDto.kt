package com.NBE3_4_2_Team4.domain.member.member.dto

import com.NBE3_4_2_Team4.domain.member.member.entity.asset.Cash
import com.NBE3_4_2_Team4.domain.member.member.entity.asset.Point
import com.fasterxml.jackson.annotation.JsonCreator

data class MemberDetailInfoResponseDto @JsonCreator constructor(
    val username: String,
    val nickname: String,
    val point: Point,
    val cash: Cash,
    val questionSize: Long,
    val answerSize: Long,
    val emailAddress: String,
    val isEmailVerified: Boolean
){
    fun emailAddress(): String? {
        return maskEmail(emailAddress)
    }

    private fun maskEmail(email: String?): String? {
        if (email == null || !email.contains("@")) {
            return email // 예외적인 경우 그냥 반환
        }
        val atIndex = email.indexOf("@")
        if (atIndex <= 1) {
            return "*".repeat(atIndex) + email.substring(atIndex)
        }
        return email.substring(0, 2) + "*".repeat(atIndex - 2) + email.substring(atIndex)
    }
}