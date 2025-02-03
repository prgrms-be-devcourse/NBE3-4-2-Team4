package com.NBE3_4_2_Team4.domain.point.entity;

import lombok.Getter;

@Getter
public enum PointCategory {
    TRANSFER("송금"),
    PURCHASE("상품구매"),
    ANSWER("답변채택"),
    ADMIN("관리자");

    private final String name;

    PointCategory(String name) {
        this.name = name;
    }
}
