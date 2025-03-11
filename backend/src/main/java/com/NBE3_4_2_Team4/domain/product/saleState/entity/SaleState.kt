package com.NBE3_4_2_Team4.domain.product.saleState.entity

import com.NBE3_4_2_Team4.global.exceptions.ServiceException

enum class SaleState {
    ALL,          // 전체 보기
    AVAILABLE,    // 구매 가능
    UNAVAILABLE,  // 구매 불가
    UPCOMING;     // 출시 예정

    companion object {
        fun fromString(name: String): SaleState {
            return runCatching { valueOf(name) }
                .getOrElse { throw ServiceException("404-1", "유효하지 않는 판매 상태 키워드 입니다.") }
        }
    }
}