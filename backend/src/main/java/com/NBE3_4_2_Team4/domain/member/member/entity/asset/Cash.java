package com.NBE3_4_2_Team4.domain.member.member.entity.asset;

import com.NBE3_4_2_Team4.global.exceptions.PointClientException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class Cash {

    @Column(name = "cash", nullable = false)
    private Long amount = 0L;

    public Cash(Long amount) {
        this.amount = amount;
    }

    public void add(Long value) {
        this.amount += value;
    }

    public void subtract(Long value) {
        if (this.amount < value) {
            throw new PointClientException("캐시가 부족합니다.");
        }
        this.amount -= value;
    }

    public static void validateAmount(long amount) {
        if (amount <= 0) throw new PointClientException("거래금액이 0이나 음수가 될 수 없습니다");
    }
}