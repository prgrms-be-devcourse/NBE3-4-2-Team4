package com.NBE3_4_2_Team4.domain.asset.main.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum AssetType {
    CASH("캐시"),
    POINT("포인트"),
    ALL("전체");

    private final String displayName;

    AssetType(String name) {
        this.displayName = name;
    }

    @JsonValue
    @Override
    public String toString() {
        return this.displayName;
    }

    @JsonCreator
    public static AssetType fromValue(String value) {
        for (AssetType assetType : values()) {
            if (assetType.name().equalsIgnoreCase(value) || assetType.getDisplayName().equalsIgnoreCase(value)) {
                return assetType;
            }
        }
        return AssetType.ALL;
    }
}