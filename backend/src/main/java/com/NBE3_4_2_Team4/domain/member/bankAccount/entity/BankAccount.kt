package com.NBE3_4_2_Team4.domain.member.bankAccount.entity

import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.global.exceptions.ServiceException
import com.NBE3_4_2_Team4.global.jpa.entity.BaseTime
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ManyToOne
import org.hibernate.annotations.DynamicUpdate

@Entity
@DynamicUpdate
data class BankAccount(

    @Column(nullable = false)
    val bankCode: String,           // 은행 코드

    @Column(nullable = false)
    val bankName: String,           // 은행 이름

    @Column(nullable = false)
    val accountNumber: String,      // 계좌 번호

    @Column(nullable = false)
    var maskedAccountNumber: String, // 마스킹 된 계좌 번호

    @Column(nullable = false)
    val accountHolder: String,      // 예금주

    var nickname: String? = null,   // 계좌 별명

    @ManyToOne(fetch = FetchType.LAZY)
    var member: Member? = null      // 회원 정보

) : BaseTime() {

    fun updateNickname(newNickname: String?) {
        nickname = if (newNickname.isNullOrBlank()) {
            if (accountNumber.isBlank() || bankName.isBlank()) {
                throw ServiceException("404-1", "계좌 번호 또는 은행 이름이 존재하지 않아 기본 계좌 별칭을 설정할 수 없습니다.")
            }
            "$bankName ${accountNumber.takeLast(4)}"
        } else {
            newNickname
        }
    }
}