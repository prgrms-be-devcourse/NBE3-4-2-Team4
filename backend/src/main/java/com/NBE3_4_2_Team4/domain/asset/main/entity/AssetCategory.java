package com.NBE3_4_2_Team4.domain.asset.main.entity;

import lombok.Getter;

@Getter
public enum AssetCategory {
        SIGN_UP("회원가입"),
        TRANSFER("송금"),
        PURCHASE("상품구매"),
        QUESTION("질문등록"),
        ANSWER("답변채택"),
        EXPIRED_QUESTION ("만료된질문"),
        REFUND("포인트반환"),
        RANKING("랭킹"),
        ADMIN("관리자"),
        ATTENDANCE("출석"),
        CASH_DEPOSIT("캐시충전"),
        CASH_REFUND("캐시반환"),
        POINT_WITHDRAWAL("포인트현금화");

        private final String displayName;

        AssetCategory(String name) {
            this.displayName = name;
        }

        @Override
        public String toString() {
            return this.displayName;
        }

        public static AssetCategory fromString(String displayName) {
            for (AssetCategory category : AssetCategory.values()) {
                if (category.displayName.equalsIgnoreCase(displayName)) {
                    return category;
                }
            }
            throw new IllegalArgumentException("Unknown enum type " + displayName);
        }
    }
