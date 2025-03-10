package com.NBE3_4_2_Team4.domain.asset.main.entity

import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@EntityListeners(
    AuditingEntityListener::class
)
class AssetHistory(
    @ManyToOne
    val member: Member,

    @Column(nullable = false)
    val amount: Long,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val assetType: AssetType,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val assetCategory: AssetCategory,

    @Column(nullable = false)
    val correlationId: String, // 연관 그룹 ID

    @ManyToOne
    val adminAssetCategory: AdminAssetCategory? = null,

    @ManyToOne
    @JoinColumn
    val counterMember: Member? = null
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @CreatedDate
    @Column(updatable = false)
    lateinit var createdAt: LocalDateTime
}