package com.NBE3_4_2_Team4.domain.member.bankAccount.service

import com.NBE3_4_2_Team4.domain.member.bankAccount.dto.BankAccountRequestDto.*
import com.NBE3_4_2_Team4.domain.member.bankAccount.dto.BankAccountResponseDto.GetBankAccount
import com.NBE3_4_2_Team4.domain.member.bankAccount.dto.BankAccountResponseDto.GetBanks
import com.NBE3_4_2_Team4.domain.member.bankAccount.repository.BankAccountRepository
import com.NBE3_4_2_Team4.global.api.iamport.v1.IamportService
import com.NBE3_4_2_Team4.global.api.iamport.v1.account.IamportAccountRequestDto.BankAccountValidator
import com.NBE3_4_2_Team4.global.exceptions.ServiceException
import com.NBE3_4_2_Team4.global.security.AuthManager.getNonNullMember
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BankAccountService(
    private val bankAccountRepository: BankAccountRepository,
    private val iamportService: IamportService
) {

    private val logger = KotlinLogging.logger {}

    @Transactional(readOnly = true)
    fun findAllBankAccount(): List<GetBankAccount> {
        val member = getNonNullMember()

        val bankAccounts = bankAccountRepository.findAllByMember(member)
        logger.info { "[${bankAccounts.size}] BankAccounts are found." }

        return bankAccounts.map { bankAccount ->
            GetBankAccount(
                bankAccountId = bankAccount.id,
                bankName = bankAccount.bankName,
                maskedAccountNumber = bankAccount.maskedAccountNumber,
                accountHolder = bankAccount.accountHolder,
                nickname = bankAccount.nickname
            )
        }
    }

    @Transactional(readOnly = true)
    fun getBanks(
    ): List<GetBanks> {

        val member = getNonNullMember()

        val accessToken = iamportService.getAccessToken(member.id)
            ?: iamportService.generateAccessToken(member.id)
            ?: throw ServiceException("500-1", "Iamport 액세스 토큰 발급을 실패했습니다.")

        val bankCodes = iamportService.getBankCodes(accessToken)
        logger.info { "[${bankCodes.size}] Banks are found." }

        return bankCodes.map { bankCode ->
            GetBanks(
                bankCode = bankCode.code,
                bankName = bankCode.name
            )
        }
    }

    @Transactional(readOnly = true)
    fun getBankAccount(
        bankAccountId: Long
    ): GetBankAccount {

        val bankAccount = bankAccountRepository.findById(bankAccountId)
            .orElseThrow { ServiceException("404-1", "해당 은행 계좌가 존재하지 않습니다.") }

        logger.info { "BankAccount Id [$bankAccountId] is found." }

        return GetBankAccount(
            bankAccountId = bankAccount.id,
            bankName = bankAccount.bankName,
            maskedAccountNumber = bankAccount.maskedAccountNumber,
            accountHolder = bankAccount.accountHolder,
            nickname = bankAccount.nickname
        )
    }

    @Transactional
    fun generateBankAccount(
        accountInfo: GenerateBankAccount
    ): GetBankAccount {

        val member = getNonNullMember()

        val accessToken = iamportService.getAccessToken(member.id)
            ?: iamportService.generateAccessToken(member.id)
            ?: throw ServiceException("500-1", "Iamport 액세스 토큰 발급을 실패했습니다.")

        verifyBankAccount(accessToken, accountInfo.bankCode, accountInfo.accountNumber, accountInfo.accountHolder)

        val bankInfo = iamportService.findBankNameByBankCode(accessToken, accountInfo.bankCode)
            ?: throw ServiceException("404-1", "은행 코드에 해당하는 은행이 존재하지 않습니다.")

        val bankAccount = bankAccountRepository.save(
            accountInfo.toBankAccount(
                member,
                maskAccountNumber(accountInfo.accountNumber),
                bankInfo.name
            )
        )

        if (bankAccount.nickname == null) {
            bankAccount.updateNickname(null)
        }

        logger.info { "BankAccount Id [${bankAccount.id}] is saved." }

        return GetBankAccount(
            bankAccountId = bankAccount.id,
            bankName = bankAccount.bankName,
            maskedAccountNumber = bankAccount.maskedAccountNumber,
            accountHolder = bankAccount.accountHolder,
            nickname = bankAccount.nickname
        )
    }

    @Transactional
    fun updateBankAccountNickname(
        bankAccountId: Long,
        updateAccountInfo: UpdateBankAccount
    ): GetBankAccount {

        val bankAccount = bankAccountRepository.findById(bankAccountId)
            .orElseThrow { ServiceException("404-1", "해당 은행 계좌가 존재하지 않습니다.") }

        bankAccount.updateNickname(updateAccountInfo.nickname)
        val updatedBankAccount = bankAccountRepository.save(bankAccount)

        logger.info { "BankAccount Nickname updated from [${bankAccount.nickname}] to [${updatedBankAccount.nickname}]." }

        return GetBankAccount(
            bankAccountId = bankAccount.id,
            bankName = bankAccount.bankName,
            maskedAccountNumber = updatedBankAccount.maskedAccountNumber,
            accountHolder = updatedBankAccount.accountHolder,
            nickname = updatedBankAccount.nickname
        )
    }

    @Transactional
    fun deleteBankAccount(
        bankAccountId: Long
    ) {

        val bankAccount = bankAccountRepository.findById(bankAccountId)
            .orElseThrow { ServiceException("404-1", "해당 은행 계좌가 존재하지 않습니다.") }

        bankAccountRepository.delete(bankAccount)
        logger.info { "BankAccount Id [${bankAccount.id}] is deleted" }
    }

    @Transactional(readOnly = true)
    fun checkBankAccountDuplicated(
        checkBankAccount: DuplicateCheckBankAccount
    ): Boolean {

        val member = getNonNullMember()

        val isExist = bankAccountRepository.existsByMemberIdAndBankCodeAndAccountNumberAndAccountHolder(
            member.id,
            checkBankAccount.bankCode,
            checkBankAccount.accountNumber,
            checkBankAccount.accountHolder
        )

        if (isExist) {
            throw ServiceException("409-1", "해당 은행 계좌는 이미 등록되었습니다.")
        }

        return isExist
    }

    private fun verifyBankAccount(
        accessToken: String,
        bankCode: String,
        accountNumber: String,
        accountHolder: String
    ): Boolean {

        val holder = iamportService.validateBankAccount(
            accessToken,
            BankAccountValidator(bankCode, accountNumber)
        ) ?: throw ServiceException("500-1", "해당 계좌의 유효성을 체크하던 중에 오류가 발생했습니다.")

        require(holder == accountHolder) {
            throw ServiceException("400-1", "해당 계좌의 예금주가 다릅니다. [$accountHolder]")
        }

        return true
    }

    private fun maskAccountNumber(
        accountNumber: String?
    ): String {

        if (accountNumber == null) {
            logger.warn { "AccountNumber is Null, setting default masked Account Number. [****]" }
            return "****"
        }

        return accountNumber.replace(Regex("(?<=\\d{3})\\d(?=\\d{3})"), "*")
    }
}