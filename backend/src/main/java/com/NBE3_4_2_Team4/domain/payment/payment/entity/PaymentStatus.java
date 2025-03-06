package com.NBE3_4_2_Team4.domain.payment.payment.entity;

public enum PaymentStatus {

    ALL("전체"),
    PAID("결제완료"),
    READY("결제대기"),
    CANCELED("결제취소");

    private final String displayName;

    PaymentStatus(String name) {
        this.displayName = name;
    }

    @Override
    public String toString() {
        return this.displayName;
    }
}