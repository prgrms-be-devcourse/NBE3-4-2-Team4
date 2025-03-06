package com.NBE3_4_2_Team4.domain.payment.payment.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentStatus {

    ALL("전체"),
    PAID("결제완료"),
    READY("결제대기"),
    CANCELED("결제취소");

    private final String displayName;

    PaymentStatus(String name) {
        this.displayName = name;
    }

    @JsonValue
    @Override
    public String toString() {
        return this.displayName;
    }

    @JsonCreator
    public static PaymentStatus fromString(String value) {
        for (PaymentStatus status : PaymentStatus.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("유효하지 않은 결제 상태입니다: " + value);
    }
}