package com.NBE3_4_2_Team4.domain.asset.main.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import lombok.Getter


enum class AssetType(val displayName: String) {
    CASH("캐시"),
    POINT("포인트"),
    ALL("전체");

    @JsonValue
    override fun toString(): String {
        return this.displayName
    }

    companion object {
        @JvmStatic
        @JsonCreator
        fun fromValue(value: String?): AssetType {
            for (assetType in entries) {
                if (assetType.displayName.equals(value, ignoreCase = true) || assetType.name
                        .equals(value, ignoreCase = true)
                ) {
                    return assetType
                }
            }
            return ALL
        }
    }
}