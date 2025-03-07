package com.NBE3_4_2_Team4.global.jpa.entity

import com.NBE3_4_2_Team4.standard.util.Ut
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass

@MappedSuperclass
abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    var _id: Long? = null

    val id: Long
        get() = _id ?: 0

    val modelName: String
        get() = Ut.str.lcfirst(this::class.simpleName!!)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is BaseEntity) return false

        if (modelName != other.modelName) return false

        return id == other.id
    }

    override fun hashCode(): Int {
        return _id?.hashCode() ?: 0
    }
}