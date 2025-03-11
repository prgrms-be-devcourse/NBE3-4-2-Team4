package com.NBE3_4_2_Team4.domain.member.bankAccount.initData

import com.NBE3_4_2_Team4.domain.member.bankAccount.dto.BankAccountRequestDto.GenerateBankAccount
import com.NBE3_4_2_Team4.domain.member.bankAccount.service.BankAccountService
import com.NBE3_4_2_Team4.domain.member.member.initData.MemberInitData
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository
import com.NBE3_4_2_Team4.global.security.AuthManager
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.core.annotation.Order
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.transaction.annotation.Transactional

@Order(3)
@Configuration
class BankAccountInitData(
    private val bankAccountService: BankAccountService,
    private val memberInitData: MemberInitData,
    private val memberRepository: MemberRepository,
    private val authManager: AuthManager
) {

    private val logger = KotlinLogging.logger {}

    @Value("\${custom.initData.member.member1.username}")
    private lateinit var member1Username: String

    @Autowired
    @Lazy
    private lateinit var self: BankAccountInitData

    @Bean
    fun bankAccountInitDataApplicationRunner(): ApplicationRunner {
        return ApplicationRunner {
            memberInitData.work()
            self.createInitBankAccounts()
        }
    }

    @Transactional
    fun createInitBankAccounts() {
        // 테스트 유저 조회
        val testUser = memberRepository.findByUsername(member1Username)
            .orElseThrow { throw IllegalStateException("Test user not found") }

        // 로그인 설정
        authManager.setLogin(testUser)

        // 기존 계좌가 존재하는 경우, 초기 데이터 생성 중단
        if (bankAccountService.findAllBankAccount().isNotEmpty()) {
            return
        }

        // 계좌 생성
        bankAccountService.generateBankAccount(
            GenerateBankAccount(
                bankCode = "031",
                accountNumber = "16613084208",
                accountHolder = "정성재"
            )
        )

        // 보안 컨텍스트 초기화
        SecurityContextHolder.clearContext()
        logger.info { "BankAccount 초기 데이터가 설정되었습니다." }
    }
}