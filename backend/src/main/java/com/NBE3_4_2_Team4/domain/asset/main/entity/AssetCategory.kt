package com.NBE3_4_2_Team4.domain.asset.main.entity


enum class AssetCategory(val displayName: String) {
    SIGN_UP("회원가입"),
    TRANSFER("송금"),
    PURCHASE("상품구매"),
    QUESTION("질문등록"),
    QUESTION_MODIFY("질문수정"),
    ANSWER("답변채택"),
    EXPIRED_QUESTION("만료된질문"),
    REFUND("포인트반환"),
    RANKING("랭킹"),
    ADMIN("관리자"),
    ATTENDANCE("출석"),
    CASH_DEPOSIT("캐시충전"),
    CASH_REFUND("캐시환불"),
    CASH_WITHDRAWAL("캐시출금");

    override fun toString(): String {
        return this.displayName
    }

    companion object {
        fun fromString(displayName: String): AssetCategory {
            for (category in entries) {
                if (category.displayName.equals(displayName, ignoreCase = true)) {
                    return category
                }
            }
            throw IllegalArgumentException("Unknown enum type $displayName")
        }
    }
}