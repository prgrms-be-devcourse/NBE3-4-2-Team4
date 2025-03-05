package com.NBE3_4_2_Team4.domain.base.genFile.entity

import com.NBE3_4_2_Team4.global.config.AppConfig
import com.NBE3_4_2_Team4.global.jpa.entity.BaseEntity
import com.NBE3_4_2_Team4.standard.util.Ut
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.EntityListeners
import jakarta.persistence.FetchType
import jakarta.persistence.ManyToOne
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@EntityListeners(AuditingEntityListener::class)
@MappedSuperclass
abstract class GenFile<T : BaseEntity> : BaseEntity() {
    enum class TypeCode {
        attachment,
        body
    }

    @CreatedDate
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    lateinit var createdAt: LocalDateTime

    @ManyToOne(fetch = FetchType.LAZY)
    lateinit var parent: T
    lateinit var typeCode: TypeCode
    var fileNo: Int = 0
    lateinit var originalFileName: String
    lateinit var metadata: String
    lateinit var fileDateDir: String
    lateinit var fileExt: String
    lateinit var fileExtTypeCode: String
    lateinit var fileExtType2Code: String
    lateinit var fileName: String
    var fileSize = 0

    val modelName: String
        get() {
            val simpleName = javaClass.simpleName
            return Ut.str.lcfirst(simpleName)
        }

    val filePath: String
        get() = "${AppConfig.getGenFileDirPath()}/${modelName}/${typeCodeAsStr}/${fileDateDir}/${fileName}"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is GenFile<*>) return false

        if (id == null || other.id == null) return false

        if (modelName != other.modelName) return false

        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    private val ownerModelName: String
        get() = modelName.replace("GenFile", "")

    private val typeCodeAsStr: String
        get() = typeCode.name

    private val ownerModelId: Long
        get() = parent.id

    val downloadUrl: String
        get() = "${AppConfig.getSiteBackUrl()}/${ownerModelName}/genFile/download/${ownerModelId}/${fileName}"

    val publicUrl: String
        get() = "${AppConfig.getSiteBackUrl()}/gen/${modelName}/${typeCodeAsStr}/${fileDateDir}/${fileName}"
}
