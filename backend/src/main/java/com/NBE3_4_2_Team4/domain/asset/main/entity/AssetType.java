package com.NBE3_4_2_Team4.domain.asset.main.entity;

import lombok.Getter;

@Getter
public enum AssetType {
    CASH("캐시"),
    POINT("포인트");

    private final String displayName;

    AssetType(String name) {
        this.displayName = name;
    }

    @Override
    public String toString() {
        return this.displayName;
    }

}
