package com.NBE3_4_2_Team4.domain.point.entity;

import lombok.Getter;

@Getter
public enum PointCategory {
    TRANSFER("송금"),
    PURCHASE("상품구매"),
    QUESTION("질문등록"),
    ANSWER("답변채택"),
    ADMIN("관리자"),
    ATTENDANCE("출석");

    private final String displayName;

    PointCategory(String name) {
        this.displayName = name;
    }

    @Override
    public String toString() {
        return this.displayName;
    }

    public static PointCategory fromString(String displayName) {
        for (PointCategory category : PointCategory.values()) {
            if (category.displayName.equalsIgnoreCase(displayName)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Unknown enum type " + displayName);
    }
}
