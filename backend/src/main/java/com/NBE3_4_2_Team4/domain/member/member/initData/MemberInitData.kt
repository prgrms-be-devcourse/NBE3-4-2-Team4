package com.NBE3_4_2_Team4.domain.member.member.initData

import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetCategory
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetHistory
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetType
import com.NBE3_4_2_Team4.domain.asset.main.repository.AssetHistoryRepository
import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.domain.member.member.entity.asset.Cash
import com.NBE3_4_2_Team4.domain.member.member.entity.asset.Point
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository
import com.NBE3_4_2_Team4.standard.constants.PointConstants
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.core.annotation.Order
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.annotation.Transactional

@Order(0)
@Slf4j
@Configuration
class MemberInitData (
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val assetHistoryRepository: AssetHistoryRepository
){
    @Value("\${custom.initData.member.admin.username}")
    private lateinit var adminUsername: String

    @Value("\${custom.initData.member.admin.password}")
    private lateinit var adminPassword: String

    @Value("\${custom.initData.member.admin.nickname}")
    private lateinit var adminNickname: String

    @Value("\${custom.initData.member.admin.email}")
    private lateinit var adminEmail: String


    @Value("\${custom.initData.member.member1.username}")
    private lateinit var member1Username: String

    @Value("\${custom.initData.member.member1.password}")
    private lateinit var member1Password: String

    @Value("\${custom.initData.member.member1.nickname}")
    private lateinit var member1Nickname: String

    @Value("\${custom.initData.member.member1.email}")
    private lateinit var member1Email: String


    @Value("\${custom.initData.member.member2.username}")
    private lateinit var member2Username: String

    @Value("\${custom.initData.member.member2.password}")
    private lateinit var member2Password: String

    @Value("\${custom.initData.member.member2.nickname}")
    private lateinit var member2Nickname: String

    @Value("\${custom.initData.member.member2.email}")
    private lateinit var member2Email: String


    @Autowired
    @Lazy
    private val self: MemberInitData? = null

    @Bean
    fun memberInitDataApplicationRunner(): ApplicationRunner {
        return ApplicationRunner { _: ApplicationArguments? ->
            self?.work()
        }
    }

    private fun saveSignUpPoint(member: Member) {
        assetHistoryRepository.save(
            AssetHistory(
                member = member,
                amount = PointConstants.INITIAL_POINT,
                assetCategory = AssetCategory.SIGN_UP,
                assetType = AssetType.POINT,
                correlationId = "asdsaaddasasddsa"
            )
        )
        member.point = Point(PointConstants.INITIAL_POINT)
    }

    private fun saveSignUpCash(member: Member) {
        assetHistoryRepository.save(
            AssetHistory(
                member = member,
                amount = PointConstants.INITIAL_POINT,
                assetCategory = AssetCategory.SIGN_UP,
                assetType = AssetType.CASH,
                correlationId = "asdsaaddasasddsadd"
            )
        )
        member.cash = Cash(10000L)
    }

    @Transactional
    fun work() {
        if (memberRepository.count() > 0) return
        val oAuth2Provider = Member.OAuth2Provider.NONE

        val admin: Member = memberRepository.save(
            Member(
                id = null,
                username = adminUsername,
                password = passwordEncoder.encode(adminPassword),
                nickname = adminNickname,
                emailAddress = adminEmail,
                emailVerified = true,
                role = Member.Role.ADMIN,
                oAuth2Provider = oAuth2Provider,
            )
        )
        saveSignUpPoint(admin)
        saveSignUpCash(admin)

        val member1: Member = memberRepository.save(
            Member(
                id = null,
                username = member1Username,
                password = passwordEncoder.encode(member1Password),
                nickname = member1Nickname,
                emailAddress = member1Email,
                emailVerified = true,
                role = Member.Role.USER,
                oAuth2Provider = oAuth2Provider,
            )
        )
        saveSignUpPoint(member1)
        saveSignUpCash(member1)

        val member2: Member = memberRepository.save(
            Member(
                id = null,
                username = member2Username,
                password = passwordEncoder.encode(member2Password),
                nickname = member2Nickname,
                emailAddress = member2Email,
                emailVerified = true,
                role = Member.Role.USER,
                oAuth2Provider = oAuth2Provider,
            )
        )
        saveSignUpPoint(member2)
        saveSignUpCash(member2)
    }
}