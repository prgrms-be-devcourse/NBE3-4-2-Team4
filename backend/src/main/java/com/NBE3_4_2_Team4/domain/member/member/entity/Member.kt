package com.NBE3_4_2_Team4.domain.member.member.entity

import com.NBE3_4_2_Team4.domain.board.answer.entity.Answer
import com.NBE3_4_2_Team4.domain.board.question.entity.Question
import com.NBE3_4_2_Team4.domain.member.OAuth2RefreshToken.entity.OAuth2RefreshToken
import com.NBE3_4_2_Team4.domain.member.member.entity.asset.Cash
import com.NBE3_4_2_Team4.domain.member.member.entity.asset.Point
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@EntityListeners(AuditingEntityListener::class)
class Member(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var role: Role,

    @Column(nullable = false)
    var oAuth2Provider: OAuth2Provider,

    @Column(nullable = false, unique = true)
    val username: String,

    @Column(nullable = false)
    var password: String = "",

    var realName: String? = null,

    var emailAddress: String? = null,

    @Column(nullable = false)
    var emailVerified: Boolean = false,

    @Column(nullable = false, unique = true)
    var nickname: String,

    @CreatedDate
    val createdAt: LocalDateTime? = null,

    @Embedded
    var point: Point = Point(0L),

    @Embedded
    var cash: Cash = Cash(0L),

    @OneToMany(mappedBy = "author")
    val questions: MutableList<Question> = mutableListOf(),

    @OneToMany(mappedBy = "author")
    val answers: MutableList<Answer> = mutableListOf(),

    @OneToOne(mappedBy = "member", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var oauth2RefreshToken: OAuth2RefreshToken? = null,

    var lastAttendanceDate: LocalDate? = null

) {
    fun isFirstLoginToday(): Boolean {
        val today = LocalDate.now()
        return lastAttendanceDate == null || today.isBefore(lastAttendanceDate)
    }

    fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(
            SimpleGrantedAuthority("ROLE_${role.name}"),
            SimpleGrantedAuthority(oAuth2Provider.name)
        )
    }

    override fun equals(other: Any?): Boolean {
        return (other is Member) && (other.id == this.id)
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    companion object {
        fun from(id: Long?, username: String, nickname: String, roleName: String, oAuth2ProviderName: String, emailAddress: String?, emailVerified: Boolean): Member {
            return Member(
                id = id,
                username = username,
                nickname = nickname,
                role = Role.getRoleByName(roleName),
                oAuth2Provider = OAuth2Provider.getOAuth2ProviderByName(oAuth2ProviderName),
                emailAddress = emailAddress,
                emailVerified = emailVerified
            )
        }
    }

    enum class Role(val value: String) {
        ADMIN("ADMIN"),
        USER("USER");

        companion object {
            fun getRoleByName(name: String): Role {
                return entries.find { it.value.equals(name, ignoreCase = true) }
                    ?: throw IllegalArgumentException("Role not found")
            }
        }
    }

    enum class OAuth2Provider(val value: String) {
        NONE("NONE"),
        KAKAO("KAKAO"),
        NAVER("NAVER"),
        GOOGLE("GOOGLE");

        companion object {
            fun getOAuth2ProviderByName(name: String): OAuth2Provider {
                return entries.find { it.value.equals(name, ignoreCase = true) }
                    ?: throw IllegalArgumentException("OAuth2Provider not found")
            }
        }
    }
}
