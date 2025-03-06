package com.NBE3_4_2_Team4.domain.member.member.entity.asset

import com.NBE3_4_2_Team4.global.exceptions.PointClientException
import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class Point(
    @Column(name = "point", nullable = false)
    private var _amount: Long = 0L
) {

    fun getAmount(): Long{
        return _amount
    }

    fun add(value: Long) {
        validateAmount(value)
        this._amount += value
    }

    fun subtract(value: Long) {
        validateAmount(value)

        if (this._amount < value) {
            throw PointClientException("포인트가 부족합니다.")
        }
        this._amount -= value
    }

    private final fun validateAmount(inputAmount: Long) {
        if (inputAmount <= 0) throw PointClientException("거래금액이 0이나 음수가 될 수 없습니다")
    }

    private final fun validateInitialAmount() {
        if (_amount < 0) throw PointClientException("최초 잔고 금액이 음수가 될 수 없습니다")
    }
}