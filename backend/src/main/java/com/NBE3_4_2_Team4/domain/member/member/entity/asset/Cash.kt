package com.NBE3_4_2_Team4.domain.member.member.entity.asset

import com.NBE3_4_2_Team4.global.exceptions.PointClientException
import jakarta.persistence.Column
import jakarta.persistence.Embeddable


@Embeddable
class Cash(
    @Column(name = "cash", nullable = false)
    var amount: Long = 0L
) {

    init {
        validateInitialAmount()
    }

//    fun getAmount(): Long{
//        return amount
//    }

    fun add(value: Long) {
        validateAmount(value)
        this.amount += value
    }

    fun subtract(value: Long) {
        validateAmount(value)

        if (this.amount < value) {
            throw PointClientException("캐시가 부족합니다.")
        }
        this.amount -= value
    }

    private final fun validateAmount(inputAmount: Long) {
        if (inputAmount <= 0) throw PointClientException("거래금액이 0이나 음수가 될 수 없습니다")
    }

    private final fun validateInitialAmount() {
        if (amount < 0) throw PointClientException("최초 잔고 금액이 음수가 될 수 없습니다")
    }
}