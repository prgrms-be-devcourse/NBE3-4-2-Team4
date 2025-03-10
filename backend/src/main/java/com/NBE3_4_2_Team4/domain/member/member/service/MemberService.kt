package com.NBE3_4_2_Team4.domain.member.member.service

import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetCategory
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetHistory
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetType
import com.NBE3_4_2_Team4.domain.asset.main.repository.AssetHistoryRepository
import com.NBE3_4_2_Team4.domain.member.OAuth2RefreshToken.entity.OAuth2RefreshToken
import com.NBE3_4_2_Team4.domain.member.OAuth2RefreshToken.repository.OAuth2RefreshTokenRepository
import com.NBE3_4_2_Team4.domain.member.member.dto.AdminLoginRequestDto
import com.NBE3_4_2_Team4.domain.member.member.dto.MemberDetailInfoResponseDto
import com.NBE3_4_2_Team4.domain.member.member.dto.NicknameUpdateRequestDto
import com.NBE3_4_2_Team4.domain.member.member.dto.SignupRequestDto
import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.domain.member.member.entity.asset.Point
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberQuerydsl
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository
import com.NBE3_4_2_Team4.global.exceptions.EmailAlreadyVerifiedException
import com.NBE3_4_2_Team4.global.exceptions.InValidPasswordException
import com.NBE3_4_2_Team4.global.exceptions.MemberNotFoundException
import com.NBE3_4_2_Team4.global.exceptions.ServiceException
import com.NBE3_4_2_Team4.global.mail.service.MailService
import com.NBE3_4_2_Team4.global.security.oauth2.OAuth2Manager
import com.NBE3_4_2_Team4.global.security.user.tempUserBeforeSignUp.TempUserBeforeSignUp
import com.NBE3_4_2_Team4.global.security.user.tempUserBeforeSignUp.TempUserBeforeSignUpService
import com.NBE3_4_2_Team4.standard.constants.PointConstants
import lombok.RequiredArgsConstructor
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.NoSuchElementException

@Service
@Transactional
@RequiredArgsConstructor
class MemberService (
    private val memberRepository: MemberRepository,
    private val memberQuerydsl: MemberQuerydsl,
    private val assetHistoryRepository: AssetHistoryRepository,
    private val passwordEncoder: PasswordEncoder,
    private val oAuth2Manager: OAuth2Manager,
    private val oAuth2RefreshTokenRepository: OAuth2RefreshTokenRepository,
    private val tempUserBeforeSignUpService: TempUserBeforeSignUpService,
    private val mailService: MailService
){
    private val log = LoggerFactory.getLogger(MemberService::class.java)

    private fun checkIfMemberExists(memberId: Long?) {
        if (!memberRepository.existsById(memberId!!)) {
            throw ServiceException("404-1", String.format("no member found with id %d", memberId))
        }
    }

    fun adminLogin(adminLoginRequestDto: AdminLoginRequestDto): Member {
        val adminUsername = adminLoginRequestDto.adminUsername
        val member = memberRepository.findByUsername(adminUsername)
            ?: throw MemberNotFoundException(adminUsername)

        if (!passwordEncoder.matches(adminLoginRequestDto.password, member.password)) {
            throw InValidPasswordException()
        }
        if (member.role != Member.Role.ADMIN) {
            throw RuntimeException("Role not allowed")
        }
        return member
    }


    fun isNicknameAvailable(nickname: String): Boolean {
        return !memberRepository.existsByUsername(nickname)
    }


    fun getLogoutUrl(member: Member): String {
        checkIfMemberExists(member.id)

        val oAuthProvider = member.oAuth2Provider

        val oAuth2LogoutService = oAuth2Manager.getOAuth2LogoutService(oAuthProvider)
            ?: throw RuntimeException("Logout service not found")
        return oAuth2LogoutService.getLogoutUrl()
    }


    fun signUp(tempToken: String?, signupRequestDto: SignupRequestDto) {
        val tempUserBeforeSignUp =
            tempUserBeforeSignUpService.getTempUserFromRedisWithJwt(tempToken)

        val member = saveMember(tempUserBeforeSignUp, signupRequestDto)

        saveOAuth2RefreshToken(member, tempUserBeforeSignUp)

        saveSignupPoints(member)

        tempUserBeforeSignUpService.deleteTempUserFromRedis(tempToken)

        val memberId = member.id!!
        val emailAddress = member.emailAddress!!

        sendAuthenticationMail(memberId, emailAddress)
    }


    fun sendAuthenticationMail(memberId: Long, emailAddress: String) {
        checkIfMemberExists(memberId)

        val authCode = UUID.randomUUID().toString()

        tempUserBeforeSignUpService.saveAuthCodeForMember(memberId, authCode)

        mailService.sendAuthenticationMail(emailAddress, memberId, authCode)
    }


    private fun saveMember(tempUser: TempUserBeforeSignUp, signupRequestDto: SignupRequestDto): Member {
        if (memberRepository.existsByUsername(tempUser.username)) {
            throw ServiceException("409-1", String.format("already exists with username %s", tempUser.username))
        }
        return memberRepository.saveAndFlush(
            Member(
                id = null,
                role = Member.Role.USER,
                oAuth2Provider = Member.OAuth2Provider.getOAuth2ProviderByName(tempUser.getProviderTypeCode()),
                username = tempUser.username,
                password = passwordEncoder.encode(tempUser.password),
                nickname = signupRequestDto.nickname,
                emailAddress = signupRequestDto.email,
                realName = tempUser.getRealName()
            )
        )
    }


    private fun saveOAuth2RefreshToken(member: Member, tempUser: TempUserBeforeSignUp) {
        oAuth2RefreshTokenRepository.save(
            OAuth2RefreshToken(
                oAuth2Id = tempUser.getOAuth2Id(),
                member = member,
                refreshToken = tempUser.getRefreshToken()
            )
        )
    }


    private fun saveSignupPoints(member: Member) {
        try {
            assetHistoryRepository.save(
                AssetHistory.builder()
                    .member(member)
                    .amount(PointConstants.INITIAL_POINT)
                    .assetCategory(AssetCategory.SIGN_UP)
                    .assetType(AssetType.POINT)
                    .correlationId("asdsaaddasasddsa")
                    .build()
            )
            member.point = Point(PointConstants.INITIAL_POINT)
        } catch (e: Exception) {
            log.error("포인트 저장 실패: {}", e.message)
        }
    }


    fun verifyEmail(memberId: Long, authCode: String): Boolean {
        checkIfMemberExists(memberId)

        val member = memberRepository.findById(memberId).orElseThrow()

        if (member.emailVerified) {
            throw EmailAlreadyVerifiedException()
        }

        val isEmailVerified = tempUserBeforeSignUpService
            .isEmailVerified(memberId, authCode)

        member.emailVerified = isEmailVerified

        return isEmailVerified
    }


    fun getMemberDetailInfo(member: Member): MemberDetailInfoResponseDto {
        checkIfMemberExists(member.id)
        return memberQuerydsl.getMemberDetailInfo(member)!!
    }


    fun updateNickname(member: Member, nicknameUpdateRequestDto: NicknameUpdateRequestDto) {
        checkIfMemberExists(member.id)

        val memberData = memberRepository.findById(member.id!!)
            .orElseThrow()

        val newNickname = nicknameUpdateRequestDto.newNickname
        memberData.nickname = newNickname
    }


    fun findByUsername(username: String): Member? {
        return memberRepository.findByUsername(username)
    }


    fun withdrawalMembership(member: Member) {
        val memberId = member.id

        checkIfMemberExists(memberId)

        val oAuth2Provider = member.oAuth2Provider

        if (oAuth2Provider != Member.OAuth2Provider.NONE) {
            val oAuth2RefreshToken = oAuth2RefreshTokenRepository
                .findByMember(member)
                ?:throw NoSuchElementException()
            val refreshToken = oAuth2RefreshToken.refreshToken

            val oAuth2DisconnectService = oAuth2Manager.getOAuth2DisconnectService(oAuth2Provider)

            if (oAuth2DisconnectService == null) {
                log.error(
                    "연동 서비스에 해당하는 DisconnectService 클래스를 찾을 수 없습니다. OAuth2Manager  OAuth2Provider : {}OAuth2Id : {}",
                    oAuth2Provider,
                    oAuth2RefreshToken.oAuth2Id
                )
            }else if (!oAuth2DisconnectService.disconnectSuccess(refreshToken)) {
                log.error(
                    "OAuth2 연동 해제 실패. (연동 해제 요청이 실패했습니다.) 해당 서비스에 직접 연결 해제를 시도하세요. OAuth2Provider : {}, OAuth2Id : {}",
                    oAuth2Provider,
                    oAuth2RefreshToken.oAuth2Id
                )
            }
        }
        memberQuerydsl.deleteMember(memberId!!)
    }
}