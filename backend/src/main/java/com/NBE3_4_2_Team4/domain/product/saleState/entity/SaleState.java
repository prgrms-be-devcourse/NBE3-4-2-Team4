package com.NBE3_4_2_Team4.domain.product.saleState.entity;

import java.util.Optional;

public enum SaleState {
    ALL,         // 전체
    ONSALE,      // 판매 중
    SOLDOUT,     // 품절
    COMINGSOON;  // 곧 출시 예정

    public static Optional<SaleState> fromString(String name) {
        return Optional.of(SaleState.valueOf(name));
    }
}
