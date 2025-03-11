package com.NBE3_4_2_Team4.domain.payment.payment.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class PaymentStatus(
    @JsonValue val displayName: String
) {

    ALL("전체"),
    PAID("결제완료"),
    READY("결제대기"),
    CANCELED("결제취소");

    override fun toString(): String = displayName

    companion object {
        @JvmStatic
        @JsonCreator
        fun fromString(value: String): PaymentStatus {
            return entries.find { it.name.equals(value, ignoreCase = true) }
                ?: throw IllegalArgumentException("유효하지 않은 결제 상태입니다: $value")
        }
    }
}