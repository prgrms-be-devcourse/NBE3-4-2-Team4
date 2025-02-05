package com.NBE3_4_2_Team4.domain.product.saleState.entity;

import java.util.Optional;

public enum SaleState {
    ONSALE,      // 판매 중
    SOLDOUT,     // 품절
    RESERVED,    // 예약 중
    COMINGSOON;  // 곧 출시 예정

    public static Optional<SaleState> fromString(String name) {
        return Optional.of(SaleState.valueOf(name));
    }
}
