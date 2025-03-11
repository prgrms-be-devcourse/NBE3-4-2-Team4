package com.NBE3_4_2_Team4.domain.member.bankAccount.repository

import com.NBE3_4_2_Team4.domain.member.bankAccount.entity.BankAccount
import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository

interface BankAccountRepository : JpaRepository<BankAccount, Long> {

    fun findAllByMember(
        member: Member
    ) : List<BankAccount>

    fun existsByMemberIdAndBankCodeAndAccountNumberAndAccountHolder(
        memberId: Long,
        bankCode: String,
        accountNumber: String,
        accountHolder: String
    ) : Boolean
}